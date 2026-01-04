package com.acc.local.dto.auth;

import com.acc.local.domain.model.auth.UserKeystone;
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
                .userId(userKeystone.getId())
                .userName(userKeystone.getName())
                .defaultProjectId(userKeystone.getDefaultProjectId())
                .domainId(userKeystone.getDomainId())
                .email(userKeystone.getEmail())
                .enabled(userKeystone.isEnabled())
                .build();
    }
}
