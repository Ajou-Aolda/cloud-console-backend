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
                .id(userKeystone.getId())
                .name(userKeystone.getName())
                .domainId(userKeystone.getDomainId())
                .defaultProjectId(userKeystone.getDefaultProjectId())
                .enabled(userKeystone.isEnabled())
                .federated(userKeystone.getFederated())
                .links(userKeystone.getLinks())
                .passwordExpiresAt(userKeystone.getPasswordExpiresAt())
                .email(userKeystone.getEmail())
                .description(userKeystone.getDescription())
                .options(userKeystone.getOptions())
                .build();
    }
}