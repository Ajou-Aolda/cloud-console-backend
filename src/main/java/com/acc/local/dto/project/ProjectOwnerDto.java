package com.acc.local.dto.project;

import com.acc.local.domain.model.auth.UserKeystone;

import lombok.Builder;

@Builder
public record ProjectOwnerDto(
	String userId,
	String userName
) {
	public static ProjectOwnerDto from(UserKeystone createdBy) {
		return ProjectOwnerDto.builder()
			.userId(createdBy.getId())
			.userName(createdBy.getName())
			.build();
	}
}
