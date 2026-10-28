package com.vanguardtech.storepulse.platform.dashboardanalytics.application.internal.outboundservices.acl;

import com.vanguardtech.storepulse.platform.assets.interfaces.acl.AssetsContextFacade;
import org.springframework.stereotype.Service;

import java.util.Optional;

/** Reads from Resource and Asset Management the resource where a device is installed. */
@Service("analyticsExternalAssetsService")
public class ExternalAssetsService {

    private final AssetsContextFacade assetsContextFacade;

    public ExternalAssetsService(AssetsContextFacade assetsContextFacade) {
        this.assetsContextFacade = assetsContextFacade;
    }

    public Optional<Long> fetchResourceIdByDeviceId(Long deviceId) {
        return assetsContextFacade.fetchResourceIdByIoTDeviceId(deviceId);
    }
}