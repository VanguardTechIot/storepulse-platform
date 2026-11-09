package com.vanguardtech.storepulse.platform.monitoring.application.internal.outboundservices.acl;

import com.vanguardtech.storepulse.platform.assets.interfaces.acl.AssetsContextFacade;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExternalResourceAssetService {

    private final AssetsContextFacade assetsContextFacade;
    private final ExternalPropertyCommunicationService externalPropertyCommunicationService;

    public ExternalResourceAssetService(AssetsContextFacade assetsContextFacade,
                                        ExternalPropertyCommunicationService externalPropertyCommunicationService) {
        this.assetsContextFacade = assetsContextFacade;
        this.externalPropertyCommunicationService = externalPropertyCommunicationService;
    }

    public Optional<Long> fetchResourceIdByDeviceId(Long deviceId) {
        return assetsContextFacade.fetchResourceIdByIoTDeviceId(deviceId);
    }

    /** Devices installed in the resources visible to the authenticated user. */
    public List<Long> fetchDeviceIdsVisibleToAuthenticatedUser() {
        return assetsContextFacade.fetchIoTDeviceIdsByResourceIds(
                externalPropertyCommunicationService.fetchResourceIdsVisibleToAuthenticatedUser());
    }

    public boolean isDeviceActive(Long deviceId) {
        return assetsContextFacade.isIoTDeviceActive(deviceId);
    }
}