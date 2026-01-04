package com.acc.local.dto.auth;

import lombok.Builder;

@Builder
public record CreateUserResponse(
    String userId,
    String userName,
    String defaultProjectId,
    String domainId,
    String email,
    boolean enabled,

    // ACC 내부 정보
    String department,
    String phoneNumber,
    Integer projectLimit
) {
    public static CreateUserResponse from(UserKeystone userKeystone) {
        return CreateUserResponse.builder()
                .userId(userKeystone.id())
                .userName(userKeystone.name())
                .defaultProjectId(userKeystone.defaultProjectId())
                .domainId(userKeystone.domainId())
                .email(userKeystone.email())
                .enabled(userKeystone.enabled())
                .build();
    }
}
