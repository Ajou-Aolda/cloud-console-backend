package com.acc.local.dto.auth;

import java.util.List;
import java.util.Map;

public record UserKeystone(
        String id,
        String name,
        String password,
        String domainId,
        String defaultProjectId,
        boolean enabled,
        List<Map<String, Object>>federated,
        Map<String, String> links,
        String passwordExpiresAt,
        String email,
        String description,
        Map<String, Object> options
) {

}
