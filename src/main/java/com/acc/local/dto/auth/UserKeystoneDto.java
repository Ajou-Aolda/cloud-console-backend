package com.acc.local.dto.auth;

import lombok.Builder;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Builder
public record UserKeystoneDto(
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

    @Deprecated
    public static UserKeystoneDto from(CreateUserRequest request) {
        if (request == null) {
            return null;
        }

        return UserKeystoneDto.builder()
                .name(extractUsernameFromEmail(request.userName()))
                .email(request.userEmail())
                .enabled(true)
                // ACC 내부 정보

                .build();
    }

    @Deprecated
    public static UserKeystoneDto from(UpdateUserRequest request) {
        if (request == null) {
            return null;
        }

        return UserKeystoneDto.builder()
                .name(request.userName())
                .email(request.userEmail())
                .description(request.description())
                .defaultProjectId(request.defaultProjectId())
                .enabled(request.enabled() != null ? request.enabled() : true)
                // ACC 내부 정보
                .build();
    }

    @Deprecated
    public static UserKeystoneDto from(SignupRequest request) {
        return UserKeystoneDto.builder()
                .name(extractUsernameFromEmail(request.email())) // email의 @ 앞부분만 name(아이디)로 사용
                .email(request.email())
                .password(request.password())
                .enabled(true)
                .build();
    }

    @Deprecated
    public static UserKeystoneDto from(AdminCreateUserRequest request) {
        return UserKeystoneDto.builder()
                .name(extractUsernameFromEmail(request.email())) // email의 @ 앞부분만 name(아이디)로 사용
                .password(request.password())
                .enabled(request.isEnabled())
                .build();
    }

    /**
     * 이메일에서 username 부분(@앞부분)만 추출
     * Skyline에서 @를 도메인으로 인식하는 문제 해결용
     */
    private static String extractUsernameFromEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        return email.substring(0, email.indexOf("@"));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        UserKeystoneDto UserKeystoneDto = (UserKeystoneDto) obj;
        return Objects.equals(id, UserKeystoneDto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
