package com.acc.local.dto.auth;

import lombok.Builder;

@Builder
public record UpdateUserResponse(
    String userId,
    String name,
    String domainId,
    String defaultProjectId,
    boolean enabled,
    String email,
    String description,

    // ACC 내부 정보
    String department,
    String phoneNumber,
    Integer projectLimit
) {
    public static UpdateUserResponse from(UserKeystone userKeystone) {
        return UpdateUserResponse.builder()
                .userId(userKeystone.id())
                .name(userKeystone.name())
                .domainId(userKeystone.domainId())
                .defaultProjectId(userKeystone.defaultProjectId())
                .enabled(userKeystone.enabled())
                .email(userKeystone.email())
                .description(userKeystone.description())
                .build();
    }
}