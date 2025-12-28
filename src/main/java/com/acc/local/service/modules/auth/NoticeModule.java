package com.acc.local.service.modules.auth;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.global.exception.ErrorCode;
import com.acc.global.exception.auth.AuthErrorCode;
import com.acc.global.exception.auth.AuthServiceException;
import com.acc.local.domain.model.auth.Notice;
import com.acc.local.dto.auth.CreateNoticeRequest;
import com.acc.local.dto.auth.CreateNoticeResponse;
import com.acc.local.dto.auth.ListNoticesResponse;
import com.acc.local.dto.auth.UpdateNoticeRequest;
import com.acc.local.dto.auth.UpdateNoticeResponse;
import com.acc.local.entity.NoticeEntity;
import com.acc.local.entity.UserDetailEntity;
import com.acc.local.repository.ports.NoticeRepositoryPort;
import com.acc.local.repository.ports.UserRepositoryPort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class NoticeModule {

    private final NoticeRepositoryPort noticeRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;

    /**
     * 관리자 공지사항 생성
     */
    @Transactional
    public CreateNoticeResponse adminCreateNotice(CreateNoticeRequest request, String creatorId) {
        // 3. 공지사항 엔티티 생성
        // 도메인 내의 generateNoticeId() 메서드를 사용하여 id 생성

        Notice notice = Notice.create(request ,creatorId);
        // 4. 저장
        NoticeEntity savedNotice = noticeRepositoryPort.save(Notice.toEntity(notice));

        // 5. 생성자 이름 조회
        String createdBy = getUserNameById(creatorId);

        // 6. 응답 생성
        return CreateNoticeResponse.from(
                savedNotice.getNoticeId(),
                savedNotice.getNoticeTitle(),
                savedNotice.getNoticeDescription(),
                createdBy,
                savedNotice.getCreatedAt(),
                savedNotice.getStartsAt(),
                savedNotice.getEndsAt()
        );
    }

    /**
     * 관리자 공지사항 목록 조회 (마커 기반 페이지네이션)
     */
    @Transactional
    public PageResponse<ListNoticesResponse> adminListNotices(PageRequest pageRequest) {
        return noticeRepositoryPort.findAllNotices(
                pageRequest.getMarker(),
                pageRequest.getDirection().name().equals("prev") ? "prev" : "next",
                pageRequest.getLimit()
        );
    }


    /**
     * 관리자 공지사항 수정
     */
    @Transactional
    public UpdateNoticeResponse adminUpdateNotice(UpdateNoticeRequest request) {
        NoticeEntity current = noticeRepositoryPort.findById(request.noticeId())
                .orElseThrow(() -> new AuthServiceException(AuthErrorCode.USER_NOT_FOUND, "공지 항목을 찾을 수 없습니다."));

        NoticeEntity updated = NoticeEntity.builder()
                .noticeId(current.getNoticeId())
                .noticeUserId(current.getNoticeUserId())
                .noticeTitle(request.title() != null ? request.title() : current.getNoticeTitle())
                .noticeDescription(request.content() != null ? request.content() : current.getNoticeDescription())
                .createdAt(current.getCreatedAt())
                .startsAt(request.startsAt() != null ? LocalDateTime.parse(request.startsAt()) : current.getStartsAt())
                .endsAt(request.endsAt() != null ? LocalDateTime.parse(request.endsAt()) : current.getEndsAt())
                .build();

        NoticeEntity saved = noticeRepositoryPort.save(updated);

        String createdBy = getUserNameById(saved.getNoticeUserId());

        return UpdateNoticeResponse.from(
                saved.getNoticeId(),
                saved.getNoticeTitle(),
                saved.getNoticeDescription(),
                createdBy,
                saved.getCreatedAt(),
                saved.getStartsAt(),
                saved.getEndsAt()
        );
    }


    /**
     * 사용자 이름 조회
     */
    private String getUserNameById(String userId) {
        return userRepositoryPort.findUserDetailById(userId)
                .map(UserDetailEntity::getUserName)
                .orElseThrow(() -> new AuthServiceException(AuthErrorCode.USER_NOT_FOUND));
    }
}
