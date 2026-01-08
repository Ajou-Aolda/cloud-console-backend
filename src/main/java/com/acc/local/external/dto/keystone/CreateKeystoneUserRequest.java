package com.acc.local.external.dto.keystone;

import lombok.Builder;

import java.util.HashMap;
import java.util.Map;

@Builder
public record CreateKeystoneUserRequest(
        String email,
        String password,
        Boolean isEnable
) {
    public Map<String, Object> toKeystoneRequest() {
        Map<String, Object> userObject = new HashMap<>();

        // 로그인 시 이메일 앞부분을 아이디로 사용하여 로그인할 수 있도록 처리
        userObject.put("name", email().split("@")[0]);
        userObject.put("password", password());
        userObject.put("enabled", isEnable());
        userObject.put("email", email());

        Map<String, Object> request = new HashMap<>();
        request.put("user", userObject);

        return request;
    }
}
