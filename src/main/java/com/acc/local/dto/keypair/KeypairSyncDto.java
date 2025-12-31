package com.acc.local.dto.keypair;

import lombok.Builder;
import lombok.Getter;

/**
 * Keypair 동기화를 위한 DTO
 * OpenStack Nova API 응답을 매핑하여 사용
 */
@Getter
@Builder
public class KeypairSyncDto {

    private String name; // 프로젝트 내 유일
    private String fingerprint;  // 전역 유일
    private String publicKey;
    private String type;

    // "projectId:fingerprint" 형식의 고유 키 생성 (비교용)
    public String getUniqueKey(String projectId) {
        return projectId + ":" + fingerprint;
    }
}

