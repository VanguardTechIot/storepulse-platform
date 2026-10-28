package com.vanguardtech.storepulse.platform.monitoring.application.internal.outboundservices.acl;

import com.vanguardtech.storepulse.platform.assets.interfaces.acl.AssetsContextFacade;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ExternalResourceAssetService {

    private final AssetsContextFacade assetsContextFacade;

    public ExternalResourceAssetService(AssetsContextFacade assetsContextFacade) {
        this.assetsContextFacade = assetsContextFacade;
    }

    public Optional<Long> fetchResourceIdByDeviceId(Long deviceId) {
        return assetsContextFacade.fetchResourceIdByIoTDeviceId(deviceId);
    }

    public boolean isDeviceActive(Long deviceId) {
        return assetsContextFacade.isIoTDeviceActive(deviceId);
    }
}
