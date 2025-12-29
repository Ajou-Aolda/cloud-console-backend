package com.acc.local.controller.docs;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.dto.network.CreateInterfaceRequest;
import com.acc.local.dto.network.ViewInterfacesResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/interfaces")
@Tag(name = "Interface", description = "인터페이스 API")
@SecurityRequirement(name = "access-token")
public interface InterfaceDocs {

    @Operation(
            summary = "인터페이스 조회",
            description = "프로젝트에 속한 인터페이스를 조회합니다.<br>"+
            "interfaceId를 통해 특정 인터페이스를 조회하거나, page를 통해 페이지 정보를 전달하여 인터페이스 목록을 조회할 수 있습니다.<br>" +
            "instanceId와 networkId를 제공하면 특정 인스턴스 혹은 네트워크에 속한 인터페이스를 조회합니다.<br>" +
            "interfaceId를 통한 상세 조회는 추후 구현할 예정입니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "인터페이스 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 - 요청 파라미터 오류",
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
                    description = "서버 오류 - 오픈스택 호출 오류",
                    content = @Content()
            )
    })
    @GetMapping
    ResponseEntity<PageResponse<ViewInterfacesResponse>> viewInterfaces(
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(description = "페이지 정보", required = false)
            PageRequest page,
            @Parameter(description = "인스턴스 ID 필터", required = false, example = "instance-1234")
            @RequestParam(required = false) String instanceId,
            @Parameter(description = "네트워크 ID 필터", required = false, example = "network-1234")
            @RequestParam(required = false) String networkId);


    @Operation(
            summary = "인터페이스 생성",
            description = """
                    새로운 인터페이스를 생성합니다.
                    
                    - 생성된 인터페이스는 지정한 네트워크 및 서브넷에 연결됩니다.
                    - 서브넷을 지정 하지 않을 시 네트워크의 기본 서브넷에 연결됩니다.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "인터페이스 생성 성공",
                    headers = @Header(
                            name = "Location",
                            description = "생성된 인터페이스의 리소스 URL",
                            schema = @Schema(type = "string", example = "/api/interfaces/{interfaceId}")
                    ),
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 - 요청 파라미터 오류",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            name = "인터페이스 이름이 유효하지 않은 경우",
                                            description = "인터페이스 이름에는 영문자, 숫자, '-', '_'만 사용할 수 있습니다.",
                                            value = """
                                            {
                                              "status": 400,
                                              "code": "ACC-NETWORK-INVALID-INTERFACE-NAME",
                                              "message": "인터페이스 이름이 유효하지 않습니다."
                                            }
                                            """
                                    ),
                                    @ExampleObject(
                                            name = "네트워크 ID가 없는 경우",
                                            description = "인터페이스는 반드시 소속 네트워크 ID를 가져야 합니다.",
                                            value = """
                                                    {
                                                      "status": 400,
                                                      "code": "ACC-NETWORK-NOT-NULL-INTERFACE-NETWORK-ID",
                                                      "message": "인터페이스의 네트워크 ID는 null이 될 수 없습니다."
                                                    }
                                            """
                                    ),
                                    @ExampleObject(
                                            name = "보안 그룹 ID가 없는 경우",
                                            description = "인터페이스는 최소 하나 이상의 보안 그룹에 속해야 합니다.",
                                            value = """
                                                    {
                                                       "status": 400,
                                                       "code": "ACC-NETWORK-NOT-NULL-INTERFACE-SECURITY-GROUP-IDS",
                                                       "message": "인터페이스의 보안 그룹 ID는 null이 될 수 없습니다."
                                                    }
                                            """
                                    ),
                                    @ExampleObject(
                                            name = "네트워크, 서브넷, 보안 그룹이 유효하지 않은 경우",
                                            description = "지정한 네트워크, 서브넷, 보안 그룹이 존재하지 않거나 접근 권한이 없는 경우",
                                            value = """
                                                    {
                                                      "status": 400,
                                                      "code": "ACC-NETWORK-NEUTRON-PORT-BAD-REQUEST",
                                                      "message": "Neutron 포트 요청이 잘못되었습니다."
                                                    }
                                                    """
                                    )
                            }

                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 - 유효하지 않은 토큰",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "권한 없음 - 프로젝트 접근 권한이 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "프로젝트 접근 권한이 없는 경우",
                                            description = "해당 프로젝트에 대한 접근 권한이 없는 경우",
                                            value = """
                                                    {
                                                      "status": 403,
                                                      "code": "ACC-NETWORK-NEUTRON-PORT-FORBIDDEN",
                                                      "message": "Neutron 포트 접근이 금지되었습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류 - 오픈스택 호출 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "External IP 할당 실패",
                                            description = "외부 네트워크 연결을 위한 External IP 할당에 실패한 경우",
                                            value = """
                                                    {
                                                      "status": 500,
                                                      "code": "ACC-NETWORK-EXTERNAL-IP-ALLOCATION-FAILED",
                                                      "message": "외부 네트워크 연결을 위한 외부 IP 할당에 실패했습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "오픈스택 네트워크 조회 실패",
                                            value = """
                                                    {
                                                      "status": 500,
                                                      "code": "ACC-NETWORK-NEUTRON-NETWORK-RETRIEVAL-FAILED",
                                                      "message": "Neutron 네트워크 조회에 실패했습니다"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "오픈스택 포트 생성 실패",
                                            value = """
                                                    {
                                                      "status": 500,
                                                      "code": "ACC-NETWORK-NEUTRON-PORT-CREATION-FAILED",
                                                      "message": "Neutron 포트 생성에 실패했습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "오픈스택 포트 삭제 실패",
                                            value = """
                                                    {
                                                      "status": 500,
                                                      "code": "ACC-NETWORK-NEUTRON-PORT-DELETION-FAILED",
                                                      "message": "Neutron 포트 삭제에 실패했습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @PostMapping
    ResponseEntity<Object> createInterface(
            @Parameter(hidden = true) Authentication authentication,
            @RequestBody
            @Parameter(description = "인터페이스 생성 요청 정보", required = true)
            CreateInterfaceRequest request);


    @Operation(
            summary = "인터페이스 삭제",
            description = "지정한 인터페이스를 삭제합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "인터페이스 삭제 성공",
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
                    description = "인터페이스 없음 - 지정한 인터페이스를 찾을 수 없음",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류 - 오픈스택 호출 오류",
                    content = @Content()
            )
    })
    @DeleteMapping
    ResponseEntity<Object> deleteInterface(
            @Parameter(hidden = true) Authentication authentication,
            @RequestParam String interfaceId);

    @Operation(
            summary = "External IP 할당",
            description = """
                    지정한 인터페이스에 External IP를 할당합니다.
                    
                    - 인터페이스에 이미 External IP가 할당되어 있으면 할당할 수 없습니다.
                    - 인터페이스가 존재하지 않으면 할당할 수 없습니다.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "IP 할당 성공",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 - 요청 파라미터 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "인터페이스 ID가 없는 경우",
                                            description = "인터페이스 ID는 필수 파라미터입니다.",
                                            value = """
                                                    {
                                                      "status": 400,
                                                      "code": "ACC-NETWORK-NOT-NULL-INTERFACE-ID",
                                                      "message": "인터페이스 ID는 null이 될 수 없습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "이미 External IP가 할당된 경우",
                                            description = "인터페이스에 이미 External IP가 할당되어 있습니다.",
                                            value = """
                                                    {
                                                      "status": 400,
                                                      "code": "ACC-NETWORK-ALREADY-HAS-EXTERNAL-IP",
                                                      "message": "해당 인터페이스에 이미 External IP가 할당되어 있습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
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
                    description = "인터페이스 없음 - 지정한 인터페이스를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "인터페이스를 찾을 수 없음",
                                            description = "지정한 ID의 인터페이스를 찾을 수 없습니다.",
                                            value = """
                                                    {
                                                      "status": 404,
                                                      "code": "ACC-NETWORK-NOT-FOUND-INTERFACE",
                                                      "message": "해당 인터페이스가 존재하지 않습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류 - 오픈스택 호출 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "External IP 할당 실패",
                                            description = "외부 네트워크 연결을 위한 External IP 할당에 실패한 경우",
                                            value = """
                                                    {
                                                      "status": 500,
                                                      "code": "ACC-NETWORK-EXTERNAL-IP-ALLOCATION-FAILED",
                                                      "message": "외부 네트워크 연결을 위한 외부 IP 할당에 실패했습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "오픈스택 네트워크 조회 실패",
                                            value = """
                                                    {
                                                      "status": 500,
                                                      "code": "ACC-NETWORK-NEUTRON-NETWORK-RETRIEVAL-FAILED",
                                                      "message": "Neutron 네트워크 조회에 실패했습니다"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "오픈스택 Floating IP 생성 실패",
                                            value = """
                                                    {
                                                      "status": 500,
                                                      "code": "ACC-NETWORK-NEUTRON-FLOATING-IP-CREATION-FAILED",
                                                      "message": "Neutron Floating IP 생성에 실패했습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @PostMapping("/external-ip")
    ResponseEntity<Object> allocateExternalIp(
            @Parameter(hidden = true) Authentication authentication,
            @RequestParam
            @Parameter(description = "인터페이스 ID", required = true, example = "interface-1234")
            String interfaceId);

    @Operation(
            summary = "External IP 해제",
            description = "지정한 인터페이스의 External IP를 해제합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "IP 해제 성공",
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
                    description = "인터페이스 없음 - 지정한 인터페이스를 찾을 수 없음",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류 - 오픈스택 호출 오류",
                    content = @Content()
            )
    })
    @DeleteMapping(path = "/external-ip")
    ResponseEntity<Object> releaseExternalIp(
            @Parameter(hidden = true) Authentication authentication,
            @RequestParam
            @Parameter(description = "인터페이스 ID", required = true, example = "interface-1234")
            String interfaceId);

    @Operation(
            summary = "SSH 포트포워딩 설정",
            description = "지정한 인터페이스에 포트포워딩을 설정합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "포트포워딩 설정 성공",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 - 요청 파라미터 오류",
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
                    description = "인터페이스 없음 - 지정한 인터페이스를 찾을 수 없음",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류 - 오픈스택 혹은 APM 호출 오류",
                    content = @Content()
            )
    })
    @PostMapping("/forwarding" )
    ResponseEntity<Object> createPortForwarding(
            @Parameter(hidden = true) Authentication authentication,
            @RequestParam
            @Parameter(description = "인터페이스 ID", required = true, example = "interface-1234")
            String interfaceId);

    @Operation(
            summary = "SSH 포트포워딩 해제",
            description = "지정한 인터페이스의 포트포워딩을 해제합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "포트포워딩 해제 성공",
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
                    description = "인터페이스 없음 - 지정한 인터페이스를 찾을 수 없음",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류 - 오픈스택 혹은 APM 호출 오류",
                    content = @Content()
            )
    })
    @DeleteMapping("/forwarding" )
    ResponseEntity<Object> deletePortForwarding(
            @Parameter(hidden = true) Authentication authentication,
            @RequestParam
            @Parameter(description = "인터페이스 ID", required = true, example = "interface-1234")
            String interfaceId);
}
