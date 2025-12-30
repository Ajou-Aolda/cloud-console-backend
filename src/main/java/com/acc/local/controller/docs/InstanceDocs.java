package com.acc.local.controller.docs;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.dto.instance.InstanceActionRequest;
import com.acc.local.dto.instance.InstanceCreateRequest;
import com.acc.local.dto.instance.InstanceQuotaResponse;
import com.acc.local.dto.instance.InstanceResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/instances")
@Tag(name = "Instance", description = "인스턴스 Server API")
@SecurityRequirement(name = "access-token")
public interface InstanceDocs {

    @Operation(
            summary = "인스턴스 목록 조회",
            description = "프로젝트에 속한 인스턴스(VM) 목록을 페이지네이션 방식으로 조회합니다.\n\n"
                    + "**페이지네이션 (마커 기반)**\n"
                    + "- marker: 이전 조회의 경계 ID (첫 조회 시 null)\n"
                    + "- direction: next(기본, 다음 페이지) | prev(이전 페이지)\n"
                    + "- limit: 페이지 크기 (기본 10, 전체 조회는 0)\n\n"
                    + "**동작 방식**\n"
                    + "- next: id > marker 기준 오름차순 조회\n"
                    + "- prev: id < marker 기준으로 조회 후 역순 정렬하여 반환\n\n"
                    + "**예시 쿼리**\n"
                    + "- 첫 페이지: GET /api/v1/instances?projectId=xxx&limit=10\n"
                    + "- 다음 페이지: GET /api/v1/instances?projectId=xxx&marker=lastId&direction=next&limit=10\n"
                    + "- 이전 페이지: GET /api/v1/instances?projectId=xxx&marker=firstId&direction=prev&limit=10"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "인스턴스 목록 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 - 파라미터 형식이 올바르지 않음",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 - 유효하지 않은 토큰",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "권한 없음 - 프로젝트 접근 권한이 없음",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류 - OpenStack Nova API 호출 실패",
                    content = @Content()
            )
    })
    @GetMapping
    ResponseEntity<PageResponse<InstanceResponse>> getInstances(
            @Parameter(hidden = true)
            Authentication authentication,
            @RequestParam
            @Parameter(description = "프로젝트 고유 ID", required = true, example = "project-uuid-1234")
            String projectId,
            @Parameter(description = "페이지네이션 정보 (marker, direction, limit)")
            PageRequest page
    );

    @Operation(
            summary = "인스턴스 생성",
            description = "새로운 인스턴스(VM)를 생성합니다.\n\n"
                    + "**필수 정보**\n"
                    + "- 인스턴스 이름 (instanceName)\n"
                    + "- 인증 방식 (keypairName 또는 password 중 택1)\n"
                    + "  - keypairName: OpenStack에 등록된 키페어 이름을 사용해야 합니다\n"
                    + "  - 새 키페어가 필요한 경우 먼저 '키페어 생성 API'를 사용하세요\n"
                    + "  - 존재하지 않는 키페어 이름 사용 시 400 Bad Request 반환\n"
                    + "- 이미지 ID (imageId)\n"
                    + "- 네트워크 연결:\n"
                    + "  - networkIds: 네트워크 UUID (자동으로 포트 생성)\n"
                    + "  - interfaceIds: 기존 포트 UUID (이미 생성된 포트 사용)\n"
                    + "  - 둘 중 최소 1개 필요, 동시 사용 가능\n"
                    + "- 인스턴스 타입 (typeId)\n\n"
                    + "**선택 정보**\n"
                    + "- 보안 그룹 ID (securityGroupIds)\n"
                    + "- 디스크 크기 (diskSize, null/0이면 이미지 기본 크기 사용)\n\n"
                    + "**쿼터 제한**\n"
                    + "- 쿼터 초과 시 403 Forbidden 반환"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "인스턴스 생성 요청 성공",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 - 필수 파라미터 누락 또는 형식 오류",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 - 유효하지 않은 토큰",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "권한 없음 - 프로젝트 접근 권한이 없거나 쿼터 한도 초과",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "리소스 없음 - 키페어, 이미지, 네트워크, 보안그룹 등을 찾을 수 없음",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류 - OpenStack Nova API 호출 실패",
                    content = @Content()
            )
    })
    @PostMapping
    ResponseEntity<Object> createInstance(
            @Parameter(hidden = true)
            Authentication authentication,
            @RequestParam
            @Parameter(description = "프로젝트 고유 ID", required = true, example = "project-uuid-1234")
            String projectId,
            @RequestBody
            @Parameter(description = "인스턴스 생성 요청 정보", required = true)
            InstanceCreateRequest request
    );

    @Operation(
            summary = "컴퓨트 쿼터 조회",
            description = "프로젝트의 컴퓨트 관련 리소스 쿼터를 조회합니다.\n\n"
                    + "**조회 정보**\n"
                    + "- vCPU 사용량 및 한도\n"
                    + "- RAM 사용량 및 한도 (단위: MB)\n"
                    + "- 인스턴스 수 사용량 및 한도\n"
                    + "- 키페어 사용량 및 한도\n\n"
                    + "**참고**\n"
                    + "- 쿼터 정보는 인스턴스 생성 전 가용 리소스 확인에 활용"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "쿼터 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 - 파라미터 형식이 올바르지 않음",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 - 유효하지 않은 토큰",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "권한 없음 - 프로젝트 접근 권한이 없음",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류 - OpenStack Nova API 호출 실패",
                    content = @Content()
            )
    })
    @GetMapping("/quota")
    ResponseEntity<InstanceQuotaResponse> getQuota(
            @Parameter(hidden = true)
            Authentication authentication,
            @RequestParam
            @Parameter(description = "프로젝트 고유 ID", required = true, example = "project-uuid-1234")
            String projectId
    );

    @Operation(
            summary = "인스턴스 작업(Action) 수행",
            description = "지정된 인스턴스에 대해 작업을 수행합니다.\n\n"
                    + "**요청 방식**\n"
                    + "- Request Body (InstanceActionRequest)의 action 필드에 수행할 작업을 명시\n"
                    + "- 선택한 action에 따라 추가 필드가 요구될 수 있음\n\n"
                    + "**사용 가능한 Action 목록**\n"
                    + "- ADD_SECURITY_GROUP: 보안 그룹 추가\n"
                    + "- CHANGE_PASSWORD: 비밀번호 변경\n"
                    + "- CONFIRM_RESIZE: 크기 변경 확인\n"
                    + "- CREATE_BACKUP: 백업 생성\n"
                    + "- CREATE_IMAGE: 이미지 생성\n"
                    + "- LOCK: 잠금\n"
                    + "- PAUSE: 일시 중지\n"
                    + "- REBOOT: 재부팅\n"
                    + "- REBUILD: 재구축\n"
                    + "- REMOVE_SECURITY_GROUP: 보안 그룹 제거\n"
                    + "- RESCUE: 복구 모드\n"
                    + "- RESIZE: 크기 변경\n"
                    + "- RESUME: 다시 시작\n"
                    + "- REVERT_RESIZE: 크기 변경 롤백\n"
                    + "- START: 시작\n"
                    + "- STOP: 정지\n"
                    + "- SUSPEND: 절전\n"
                    + "- UNLOCK: 잠금 해제\n"
                    + "- UNPAUSE: 일시 중지 해제\n"
                    + "- UNRESCUE: 복구 모드 해제\n"
                    + "- FORCE_DELETE: 강제 삭제\n"
                    + "- RESTORE: 복원\n"
                    + "- SHELVE: 보관\n"
                    + "- SHELVE_OFFLOAD: 보관(오프로드)\n"
                    + "- UNSHELVE: 보관 해제\n\n"
                    + "**참고**\n"
                    + "- 인스턴스의 현재 상태에 따라 수행 가능한 작업이 제한될 수 있음\n"
                    + "- 상태가 맞지 않을 경우 409 Conflict 반환"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "인스턴스 작업 요청 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 - 지원되지 않는 Action이거나 필수 파라미터 누락",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 - 유효하지 않은 토큰",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "권한 없음 - 프로젝트 접근 권한이 없음",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "리소스 없음 - 지정한 인스턴스를 찾을 수 없음",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "상태 오류 - 현재 인스턴스 상태에서는 해당 동작을 수행할 수 없음",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류 - OpenStack Nova API 호출 실패",
                    content = @Content()
            )
    })
    @PostMapping("/action")
    ResponseEntity<Object> controlInstance(
            @Parameter(hidden = true)
            Authentication authentication,
            @RequestParam
            @Parameter(description = "프로젝트 고유 ID", required = true, example = "project-uuid-1234")
            String projectId,
            @RequestParam("instanceId")
            @Parameter(description = "인스턴스 고유 ID", required = true, example = "vm-uuid-1234-5678")
            String instanceId,
            @RequestBody
            @Parameter(description = "인스턴스 작업 요청 정보", required = true)
            InstanceActionRequest request
    );
}
