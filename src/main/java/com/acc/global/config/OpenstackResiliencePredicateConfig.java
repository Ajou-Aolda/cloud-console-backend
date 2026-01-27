package com.acc.global.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * 목적:
 * - 기존 yml 기반(횟수/윈도우/대기시간/임계치) 유지
 * - HTTP 응답(WebClientResponseException)만 코드로 분기해 정책을 더 정교하게 적용

 * 정책:
 * - CircuitBreaker:
 *   - 4xx는 failure로 기록하지 않음 (ignore)
 *   - 5xx도 전부 record 하지 않음
 *     -> 500/502/503/504만 failure로 기록 (record)
 *     -> 그 외 5xx(501/505/507 등)는 record 하지 않음 (ignore)
 *   - 네트워크/타임아웃/IO는 기존 yml recordExceptions 그대로 유지

 * - Retry:
 *   - 기존 yml retryExceptions 그대로 유지 (네트워크/타임아웃/IO 중심)
 *   - HTTP 응답(WebClientResponseException)은 4xx는 retry 하지 않음
 *   - Get류에 한해서 500/502/503/504만 retry 허용
 *     -> 그 외 5xx는 retry 하지 않음
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class OpenstackResiliencePredicateConfig {

    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final RetryRegistry retryRegistry;

    @PostConstruct
    public void patchResilience4jConfigs() {

        patchCircuitBreakerConfig("default-config");
        patchRetryConfig("default-get");
//        patchRetryConfig("default-post");
        log.info("[RESILIENCE-PREDICATE] patched: cbConfigs=[default-config], retryConfigs=[default-get]");
    }

    private void patchCircuitBreakerConfig(String configName) {
        Optional<CircuitBreakerConfig> opt = circuitBreakerRegistry.getConfiguration(configName);
        if (opt.isEmpty()) {
            log.warn("[RESILIENCE-PREDICATE] CircuitBreaker config '{}' not found. skip", configName);
            return;
        }

        CircuitBreakerConfig base = opt.get();
        Predicate<Throwable> basePredicate = base.getRecordExceptionPredicate();

        // 4xx ignore, 5xx record, 그 외는 기존 yml recordExceptions predicate로 판단
        Predicate<Throwable> patchedPredicate = t -> {
            if (t instanceof WebClientResponseException wcre) {
                return isShouldRecord(wcre);
            }
            return basePredicate.test(t);
        };

        CircuitBreakerConfig patched = CircuitBreakerConfig.from(base)
                .recordException(patchedPredicate)
                .build();


        Predicate<Throwable> before = base.getRecordExceptionPredicate();

        circuitBreakerRegistry.addConfiguration(configName, patched);

        Predicate<Throwable> after =
                circuitBreakerRegistry.getConfiguration(configName).orElseThrow()
                        .getRecordExceptionPredicate();
        log.info(
                "[CB-CONFIG-PRED] config={} beforeId={} afterId={} changed={}",
                configName,
                System.identityHashCode(before),
                System.identityHashCode(after),
                before != after
        );
        log.info("[RESILIENCE-PREDICATE] CircuitBreaker config '{}' patched (record 500/502/503/504 for 5xx; otherwise keep yml)", configName);
    }

    private void patchRetryConfig(String configName) {
        Optional<RetryConfig> opt = retryRegistry.getConfiguration(configName);
        if (opt.isEmpty()) {
            log.warn("[RESILIENCE-PREDICATE] Retry config '{}' not found. skip", configName);
            return;
        }

        RetryConfig base = opt.get();
        Predicate<Throwable> basePredicate = base.getExceptionPredicate();

        Predicate<Throwable> patchedPredicate = t -> {
            if (t instanceof WebClientResponseException wcre) {
                return isShouldRecord(wcre);
            }
            return basePredicate.test(t);
        };

        RetryConfig patched = RetryConfig.from(base)
                .retryOnException(patchedPredicate)
                .build();

        retryRegistry.addConfiguration(configName, patched);
        log.info("[RESILIENCE-PREDICATE] Retry config '{}' patched (retry only 500/502/503/504 for 5xx; otherwise keep yml)", configName);
    }

    private boolean isShouldRecord(WebClientResponseException wcre) {
        var sc = wcre.getStatusCode();
        if (sc.is4xxClientError()) return false;
        if (sc.is5xxServerError()) return shouldRecord5xx(sc.value());
        return false;
    }

    private boolean shouldRecord5xx(int code) {
        return code == 500 || code == 502 || code == 503 || code == 504;
    }
}