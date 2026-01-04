package com.acc.local.dto.auth;

import com.acc.local.domain.model.auth.UserKeystone;
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
                .userId(userKeystone.getId())
                .name(userKeystone.getName())
                .domainId(userKeystone.getDomainId())
                .defaultProjectId(userKeystone.getDefaultProjectId())
                .enabled(userKeystone.isEnabled())
                .email(userKeystone.getEmail())
                .description(userKeystone.getDescription())
                .build();
    }
}