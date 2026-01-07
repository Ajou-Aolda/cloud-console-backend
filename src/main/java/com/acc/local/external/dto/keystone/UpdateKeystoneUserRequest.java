package com.acc.local.external.dto.keystone;

import com.acc.local.external.modules.keystone.KeystoneAPIUtils;
import lombok.Builder;

import java.util.HashMap;
import java.util.Map;

@Builder
public record UpdateKeystoneUserRequest(
        String name,
        String email,
        String password,
        String description,
        String defaultProjectId,
        Boolean isEnable
) {
    public Map<String, Object> toKeystoneRequest() {
        Map<String, Object> userObject = new HashMap<>();

        if (name() != null) {
            userObject.put("name", name());
        }
        if (email() != null) {
            userObject.put("email", email());
        }
        if (password() != null) {
            userObject.put("password", password());
        }
        if (description() != null) {
            userObject.put("description", description());
        }
        if (defaultProjectId() != null) {
            userObject.put("default_project_id", defaultProjectId());
        }
        userObject.put("enabled", isEnable());

        Map<String, Object> request = new HashMap<>();
        request.put("user", userObject);

        return request;
    }
}
