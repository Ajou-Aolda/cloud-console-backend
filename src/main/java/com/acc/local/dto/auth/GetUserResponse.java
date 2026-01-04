package com.acc.local.dto.auth;

import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public record GetUserResponse(
    String id,
    String name,
    String domainId,
    String defaultProjectId,
    boolean enabled,
    List<Map<String, Object>> federated,
    Map<String, String> links,
    String passwordExpiresAt,
    String email,
    String description,
    Map<String, Object> options,

    // ACC 내부 정보
    String department,
    String phoneNumber,
    Integer projectLimit
) {
    public static GetUserResponse from(UserKeystone userKeystone) {
        return GetUserResponse.builder()
                .id(userKeystone.id())
                .name(userKeystone.name())
                .domainId(userKeystone.domainId())
                .defaultProjectId(userKeystone.defaultProjectId())
                .enabled(userKeystone.enabled())
                .federated(userKeystone.federated())
                .links(userKeystone.links())
                .passwordExpiresAt(userKeystone.passwordExpiresAt())
                .email(userKeystone.email())
                .description(userKeystone.description())
                .options(userKeystone.options())
                .build();
    }
}