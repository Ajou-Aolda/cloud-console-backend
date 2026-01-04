package com.acc.local.external.ports;

import com.acc.local.dto.instance.InterfaceAttachmentRequest;
import com.acc.local.dto.instance.InterfaceAttachmentResponse;

import java.util.List;

public interface NovaServerInterfaceExternalPort {

    List<InterfaceAttachmentResponse> callListInterfaces(String keystoneToken, String projectId, String instanceId);
    InterfaceAttachmentResponse callCreateInterface(String keystoneToken, String projectId, String instanceId, InterfaceAttachmentRequest request);
    void callDetachInterface(String keystoneToken, String projectId, String instanceId, String interfaceId);
}

