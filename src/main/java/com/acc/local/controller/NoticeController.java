package com.acc.local.controller;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.global.security.jwt.JwtInfo;
import com.acc.local.controller.docs.NoticeDocs;
import com.acc.local.dto.auth.CreateNoticeRequest;
import com.acc.local.dto.auth.CreateNoticeResponse;
import com.acc.local.dto.auth.GetNoticeResponse;
import com.acc.local.dto.auth.ListNoticesResponse;
import com.acc.local.dto.auth.ListRolesResponse;
import com.acc.local.service.ports.NoticeServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NoticeController implements NoticeDocs {

    private final NoticeServicePort noticeServicePort;

    @Override
    public ResponseEntity<CreateNoticeResponse> createNotice(CreateNoticeRequest request, Authentication authentication) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        String userId = jwtInfo.getUserId();
        CreateNoticeResponse response = noticeServicePort.adminCreateNotice(request, userId);
        return ResponseEntity.status(201).body(response);
    }

    @Override
    public ResponseEntity<PageResponse<ListNoticesResponse>> listNotices(PageRequest page, Authentication authentication) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        String requesterId = jwtInfo.getUserId();

        PageResponse<ListNoticesResponse> response = noticeServicePort.adminListNotices(page, requesterId);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<GetNoticeResponse> getNotice(String noticeId, Authentication authentication) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        String requesterId = jwtInfo.getUserId();

        GetNoticeResponse response = noticeServicePort.adminGetNotice(noticeId, requesterId);
        return ResponseEntity.ok(response);
    }
}

