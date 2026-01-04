package com.acc.local.service.modules.instance;

import com.acc.local.dto.instance.InterfaceAttachmentRequest;
import com.acc.local.dto.instance.InterfaceAttachmentResponse;
import com.acc.local.external.ports.NovaServerInterfaceExternalPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class InstanceInterfaceModule {

    private final NovaServerInterfaceExternalPort novaServerInterfaceExternalPort;

    public List<InterfaceAttachmentResponse> listInterfaces(String keystoneToken, String projectId, String instanceId) {
        return novaServerInterfaceExternalPort.callListInterfaces(keystoneToken, projectId, instanceId);
    }

    public InterfaceAttachmentResponse createInterface(String keystoneToken, String projectId, String instanceId, InterfaceAttachmentRequest request) {
        return novaServerInterfaceExternalPort.callCreateInterface(keystoneToken, projectId, instanceId, request);
    }

    public void detachInterface(String keystoneToken, String projectId, String instanceId, String interfaceId) {
        novaServerInterfaceExternalPort.callDetachInterface(keystoneToken, projectId, instanceId, interfaceId);
    }
}

