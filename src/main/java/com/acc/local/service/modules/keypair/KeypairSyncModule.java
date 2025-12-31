package com.acc.local.service.modules.keypair;

import com.acc.local.dto.keypair.KeypairSyncDto;
import com.acc.local.entity.KeypairEntity;
import com.acc.local.entity.ProjectEntity;
import com.acc.local.external.ports.KeypairExternalPort;
import com.acc.local.repository.ports.KeypairRepositoryPort;
import com.acc.local.repository.ports.ProjectRepositoryPort;
import com.acc.local.service.modules.auth.AuthModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeypairSyncModule {

    private final KeypairExternalPort keypairExternalPort;
    private final KeypairRepositoryPort keypairRepositoryPort;
    private final ProjectRepositoryPort projectRepositoryPort;
    private final AuthModule authModule;

    /**
     * 모든 프로젝트의 Keypair 동기화
     * 매일 새벽 5시(한국시간)에 실행되며, 분산 환경에서 중복 실행 방지를 위해 ShedLock 사용
     *
     * OpenStack CLI나 대시보드를 통해 직접 생성/삭제된 Keypair를 감지하여
     * BFF DB와의 정합성을 유지합니다.
     */
//    @Scheduled(cron = "0 */10 * * * *")  // 매 10분마다 실행
    @Scheduled(cron = "0 0 5 * * *", zone = "Asia/Seoul")  // 매일 새벽 5시 (한국시간)
    @SchedulerLock(
        name = "KeypairSyncTask",
        lockAtMostFor = "30m",   // 최대 30분
        lockAtLeastFor = "1m"    // 최소 1분 (중복 실행 방지)
    )
    public void syncAllProjects() {
        List<ProjectEntity> projects = projectRepositoryPort.findAll();

        if (projects.isEmpty()) {
            log.info("[Keypair Sync] No projects found. Skipping sync.");
            return;
        }

        int totalAdded = 0;
        int totalDeleted = 0;
        int totalUpdated = 0;
        int errorCount = 0;

        for (ProjectEntity project : projects) {
            try {
                SyncResult result = syncProjectKeypairs(project);
                totalAdded += result.added();
                totalDeleted += result.deleted();
                totalUpdated += result.updated();

            } catch (Exception e) {
                errorCount++;
                log.error("[Keypair Sync] Failed for project: {}", project.getProjectId(), e);
            }
        }
        log.info("[Keypair Sync] Completed: +{} added, -{} deleted, ~{} updated, {} errors", totalAdded, totalDeleted, totalUpdated, errorCount);
    }

    /**
     * 특정 프로젝트의 Keypair 동기화
     *
     * @param project 동기화할 프로젝트
     * @return 동기화 결과 (추가/삭제/수정 개수)
     */
    @Transactional
    public SyncResult syncProjectKeypairs(ProjectEntity project) {
        String projectId = project.getProjectId();

        try {
            // 1. 프로젝트 스코프 토큰 발급
            String ownerUserId = project.getOwnerKeystoneId();
            String token = authModule.issueProjectScopeToken(projectId, ownerUserId);

            // 2. OpenStack에서 Keypair 목록 조회
            List<KeypairSyncDto> osKeypairs = keypairExternalPort.listKeypairsByProject(token);

            // 3. DB에서 Keypair 목록 조회
            List<KeypairEntity> dbKeypairs = keypairRepositoryPort.findAllByProjectId(projectId);

            // 4. Map으로 변환 (O(1) 검색)
            Map<String, KeypairSyncDto> osMap = osKeypairs.stream()
                .collect(Collectors.toMap(
                    KeypairSyncDto::getFingerprint,  // fingerprint를 키로 사용
                    Function.identity()
                ));

            Map<String, KeypairEntity> dbMap = dbKeypairs.stream()
                .collect(Collectors.toMap(
                    KeypairEntity::getKeypairId,    // fingerprint (PK)
                    Function.identity()
                ));

            // 5. 차이 계산
            Set<String> osFingerprints = osMap.keySet();
            Set<String> dbFingerprints = dbMap.keySet();

            // OpenStack에만 있음 -> DB 추가
            List<KeypairEntity> toAdd = osFingerprints.stream()
                .filter(fp -> !dbFingerprints.contains(fp))
                .map(fp -> {
                    KeypairSyncDto dto = osMap.get(fp);
                    return KeypairEntity.builder()
                        .keypairId(dto.getFingerprint())
                        .keypairName(dto.getName())
                        .project(project)
                        .build();
                })
                .toList();

            // DB에만 있음 -> DB 삭제
            List<KeypairEntity> toDelete = dbFingerprints.stream()
                .filter(fp -> !osFingerprints.contains(fp))
                .map(dbMap::get)
                .toList();

            // 이름 변경 감지 (fingerprint는 같지만 name이 다른 경우)
            List<KeypairEntity> toUpdate = osFingerprints.stream()
                .filter(dbFingerprints::contains)
                .filter(fp -> {
                    String osName = osMap.get(fp).getName();
                    String dbName = dbMap.get(fp).getKeypairName();
                    return !osName.equals(dbName);
                })
                .map(fp -> {
                    KeypairEntity entity = dbMap.get(fp);
                    return KeypairEntity.builder()
                        .keypairId(entity.getKeypairId())
                        .keypairName(osMap.get(fp).getName())
                        .project(entity.getProject())
                        .build();
                })
                .toList();

            // 6. 배치 실행
            if (!toAdd.isEmpty()) {
                keypairRepositoryPort.saveAll(toAdd);
                log.info("[Keypair Sync] Project {}: Added {} keypairs",
                    projectId, toAdd.size());
            }

            if (!toDelete.isEmpty()) {
                keypairRepositoryPort.deleteAll(toDelete);
                log.warn("[Keypair Sync] Project {}: Deleted {} keypairs (orphan records)",
                    projectId, toDelete.size());
            }

            if (!toUpdate.isEmpty()) {
                keypairRepositoryPort.saveAll(toUpdate);
                log.info("[Keypair Sync] Project {}: Updated {} keypair names",
                    projectId, toUpdate.size());
            }
            return new SyncResult(toAdd.size(), toDelete.size(), toUpdate.size());

        } catch (Exception e) {
            log.error("[Keypair Sync] Failed to sync project {}: {}", projectId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 동기화 결과
     *
     * @param added 추가된 Keypair 개수
     * @param deleted 삭제된 Keypair 개수
     * @param updated 수정된 Keypair 개수
     */
    public record SyncResult(int added, int deleted, int updated) {}
}

