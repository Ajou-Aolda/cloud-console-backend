package com.acc.local.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record CreateNoticeRequest(

    @Schema(description = "공지의 제목", example = "장학 공지") String title,
    @Schema(description = "공지의 내용", example = "아올다 회원은 전액 장학을 지원합니다.") String content,
    @Schema(description = "공지 시작 시간", example = "2025-01-01") String startsAt,
    @Schema(description = "공지 종료 시간", example = "2025-12-30") String endsAt
) {
}