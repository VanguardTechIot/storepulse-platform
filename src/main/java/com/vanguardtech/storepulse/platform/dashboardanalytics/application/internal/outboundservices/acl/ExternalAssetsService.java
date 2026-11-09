package com.vanguardtech.storepulse.platform.dashboardanalytics.application.internal.outboundservices.acl;

import com.vanguardtech.storepulse.platform.assets.interfaces.acl.AssetsContextFacade;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/** Reads from Resource and Asset Management which devices are installed in some resources. */
@Service("analyticsExternalAssetsService")
public class ExternalAssetsService {

    private final AssetsContextFacade assetsContextFacade;

    public ExternalAssetsService(AssetsContextFacade assetsContextFacade) {
        this.assetsContextFacade = assetsContextFacade;
    }

    public List<Long> fetchDeviceIdsByResourceIds(Collection<Long> resourceIds) {
        return assetsContextFacade.fetchIoTDeviceIdsByResourceIds(resourceIds);
    }
}