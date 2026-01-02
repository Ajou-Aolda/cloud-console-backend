package com.acc.local.controller.docs;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.dto.auth.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/admin/notices")
@Tag(name = "Admin Notices", description = "관리자 공지 관리 API")
@SecurityRequirement(name = "access-token")
public interface NoticeDocs {

    @Operation(
            summary = "공지 생성",
            description = "관리자가 공지를 생성합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "공지 생성 성공",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 - 필수 파라미터가 누락되었습니다.",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 - 유효하지 않은 토큰입니다.",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "권한 없음 - 관리자 권한이 필요한 기능입니다.",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류 - ACC 서버 에러가 발생했습니다.",
                    content = @Content()
            )
    })
    @PostMapping("")
    ResponseEntity<CreateNoticeResponse> createNotice(
            @RequestBody
            @Parameter(description = "공지 생성 요청 정보", required = true)
            CreateNoticeRequest request,
            @Parameter(hidden = true) Authentication authentication);

    @Operation(
            summary = "[관리자] 공지 목록 조회",
            description = "관리자가 공지 목록을 조회합니다.\n\n"
                    + "- 페이지네이션 (마커 기반)\n"
                    + "예시 쿼리\n"
                    + "- 첫 조회: GET /api/v1/admin/notices?limit=10\n"
                    + "- 다음 페이지: GET /api/v1/admin/notices?marker={lastId}&direction=next&limit=10\n"
                    + "- 이전 페이지: GET /api/v1/admin/notices?marker={firstId}&direction=prev&limit=10"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "공지 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "contents": [
                                        {
                                          "noticeId": "550e8400-e29b-41d4-a716-446655440000",
                                          "title": "장학 공지",
                                          "content": "아올다 회원은 전액 장학을 지원합니다.",
                                          "createdBy": "admin",
                                          "createdAt": "2025-01-01T09:00:00",
                                          "startsAt": "2025-01-01T00:00:00",
                                          "endsAt": "2025-12-31T23:59:59"
                                        }
                                      ],
                                      "first": true,
                                      "last": false,
                                      "size": 1,
                                      "nextMarker": "550e8400-e29b-41d4-a716-446655440001",
                                      "prevMarker": null
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 - 잘못된 파라미터 형식입니다.",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 - 유효하지 않은 토큰입니다.",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "권한 없음 - 관리자 권한이 필요한 기능입니다.",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류 - ACC 서버 내부 에러가 발생했습니다.",
                    content = @Content()
            )
    })
    @GetMapping("")
    ResponseEntity<PageResponse<ListNoticesResponse>> listNotices(
            @Parameter(
                    description = "페이지네이션 파라미터\n"
                            + "- marker: 경계 ID (첫 조회 시 null)\n"
                            + "- direction: next(기본) | prev\n"
                            + "- limit: 데이터 수",
                    required = false
            )
            PageRequest page,
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(
            summary = "[관리자] 공지 상세 조회",
            description = "관리자가 특정 공지의 상세 정보를 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "공지 상세 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "noticeId": "550e8400-e29b-41d4-a716-446655440000",
                                      "title": "장학 공지",
                                      "content": "아올다 회원은 전액 장학을 지원합니다.",
                                      "createdBy": "admin",
                                      "createdAt": "2025-01-01T09:00:00",
                                      "startsAt": "2025-01-01T00:00:00",
                                      "endsAt": "2025-12-31T23:59:59"
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 - 잘못된 파라미터 형식입니다.",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 - 유효하지 않은 토큰입니다.",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "권한 없음 - 관리자 권한이 필요한 기능입니다.",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "리소스 없음 - 공지를 찾을 수 없습니다.",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류 - ACC 서버 내부 에러가 발생했습니다.",
                    content = @Content()
            )
    })
    @GetMapping("/{noticeId}")
    ResponseEntity<GetNoticeResponse> getNotice(
            @PathVariable
            @Parameter(description = "조회할 공지 ID", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            String noticeId,
            @Parameter(hidden = true) Authentication authentication
    );
}
