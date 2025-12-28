package com.acc.local.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record UpdateNoticeRequest(
        @Schema(description = "수정할 공지 ID")
        String noticeId,
        @Schema(description = "공지 제목(선택)")
        String title,
        @Schema(description = "공지 내용(선택)")
        String content,
        @Schema(description = "공지 시작 시각 (ISO-8601, 선택)", example = "2025-01-01T09:00:00")
        String startsAt,
        @Schema(description = "공지 종료 시각 (ISO-8601, 선택)", example = "2025-01-31T18:00:00")
        String endsAt
) {
}
