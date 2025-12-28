package com.acc.local.dto.project;

import com.acc.local.dto.project.quota.ProjectQuotaRequest;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateProjectRequest(
        @Schema(description = "프로젝트 이름") String projectName,
        @Schema(description = "프로젝트 설명") String projectDescription,
		@Schema(description = "프로젝트 가용량") ProjectQuotaRequest quota,
        @Schema(description = "프로젝트 담당자 ID (Keystone 사용자 ID = ACC DB UserDetail.userId). 생성 시 해당 사용자에게 PROJECT_ADMIN 역할이 부여되며, 기본 네트워크가 오너 스코프 토큰으로 생성됩니다.") String projectOwnerId
) { }
