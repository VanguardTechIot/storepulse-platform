package com.vanguardtech.storepulse.platform.safety.application.internal.outboundservices.acl;

import com.vanguardtech.storepulse.platform.assets.interfaces.acl.AssetsContextFacade;
import com.vanguardtech.storepulse.platform.propertycommunication.interfaces.acl.PropertyCommunicationContextFacade;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("safetyExternalAssetsService")
public class ExternalAssetsService {

    private final AssetsContextFacade assetsContextFacade;
    private final PropertyCommunicationContextFacade propertyCommunicationContextFacade;

    public ExternalAssetsService(AssetsContextFacade assetsContextFacade,
                                 PropertyCommunicationContextFacade propertyCommunicationContextFacade) {
        this.assetsContextFacade = assetsContextFacade;
        this.propertyCommunicationContextFacade = propertyCommunicationContextFacade;
    }

    public List<Long> fetchDeviceIdsVisibleToAuthenticatedUser() {
        return assetsContextFacade.fetchIoTDeviceIdsByResourceIds(
                propertyCommunicationContextFacade.fetchResourceIdsVisibleToAuthenticatedUser());
    }
}