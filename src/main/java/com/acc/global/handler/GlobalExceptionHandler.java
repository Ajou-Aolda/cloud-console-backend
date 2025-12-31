package com.acc.global.handler;

import com.acc.global.exception.AccBaseException;
import com.acc.global.exception.ErrorCode;
import com.acc.global.exception.ErrorResponse;
import com.acc.global.exception.common.CommonErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.PrintWriter;
import java.io.StringWriter;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @ExceptionHandler(AccBaseException.class)
    public ResponseEntity<ErrorResponse> handleAccBaseException(AccBaseException ex, HttpServletRequest request) {
        setupExceptionContext(ex, request);
        ErrorCode errorCode = ex.getErrorCode();
        HttpStatus status = HttpStatus.valueOf(errorCode.getStatus());

        if (ex.getCause() instanceof WebClientResponseException webEx) {
            handleWrappedExternalException(ex, webEx);
        } else {
            if (status.is5xxServerError()) {
                log.error("[Exception] Server Error - Code: {}, Message: {}", errorCode.getCode(), ex.getCustomMessage(), ex);
            } else {
                log.warn("[Exception] Client Error - Code: {}, Message: {}", errorCode.getCode(), ex.getCustomMessage());
            }
        }
        
        cleanupExceptionContext();
        return ResponseEntity.status(status).body(new ErrorResponse(errorCode, ex.getCustomMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex, HttpServletRequest request) {
        setupExceptionContext(ex, request);
        
        log.error("[Exception] Unhandled System Error - {}", ex.getMessage(), ex);
        //ex.printStackTrace();
        cleanupExceptionContext();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(CommonErrorCode.INTERNAL_FAILURE, ex.getMessage()));
    }

    private void handleWrappedExternalException(AccBaseException parentEx, WebClientResponseException cause) {
        HttpStatus externalStatus = HttpStatus.valueOf(cause.getStatusCode().value());
        String errorCode = parentEx.getErrorCode().getCode();
        MDC.put("externalStatus", String.valueOf(externalStatus.value()));
        MDC.put("externalBody", cause.getResponseBodyAsString());

        if (externalStatus.is5xxServerError()) {
            log.error("[Exception] External System Failure (Wrapped) - Code: {}, External: {}", errorCode, externalStatus, parentEx);
        } else if (externalStatus == HttpStatus.BAD_REQUEST) {
            log.error("[Exception] External Bad Request (Wrapped) - Possible Logic Bug - Code: {}, External: 400", errorCode, parentEx);
        } else if (externalStatus == HttpStatus.NOT_FOUND || externalStatus == HttpStatus.CONFLICT) {
            log.warn("[Exception] External Resource Issue (Wrapped) - Code: {}, External: {}", errorCode, externalStatus);
        } else {
            log.warn("[Exception] External Client Error (Wrapped) - Code: {}, External: {}", errorCode, externalStatus);
        }
    }

    private void setupExceptionContext(Exception ex, HttpServletRequest request) {
        MDC.put("type", "EXCEPTION");
        MDC.put("exceptionClass", ex.getClass().getSimpleName());
        MDC.put("path", request.getRequestURI());
        
        if (MDC.get("userId") == null) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getName() != null) {
                MDC.put("userId", auth.getName());
            } else {
                MDC.put("userId", "anonymous");
            }
        }
        
        MDC.put("stackTrace", getStackTraceSnippet(ex));
    }
    
    private void cleanupExceptionContext() {
        MDC.remove("exceptionClass");
        MDC.remove("stackTrace");
        MDC.remove("externalStatus");
        MDC.remove("externalBody");
        MDC.remove("type");
        MDC.remove("path");
    }

    private String getStackTraceSnippet(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        String stackTrace = sw.toString();
        return stackTrace.length() > 1000 ? stackTrace.substring(0, 1000) + "..." : stackTrace;
    }
}

