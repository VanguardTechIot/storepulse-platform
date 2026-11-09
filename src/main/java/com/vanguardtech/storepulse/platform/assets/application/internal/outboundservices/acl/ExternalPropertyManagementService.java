package com.vanguardtech.storepulse.platform.assets.application.internal.outboundservices.acl;

import com.vanguardtech.storepulse.platform.propertymanagement.interfaces.acl.PropertyManagementContextFacade;
import org.springframework.stereotype.Service;

import java.util.List;

/** Reads from Property Management which resources are linked to the units of an administrator's gallery. */
@Service("assetsExternalPropertyManagementService")
public class ExternalPropertyManagementService {

    private final PropertyManagementContextFacade propertyManagementContextFacade;

    public ExternalPropertyManagementService(PropertyManagementContextFacade propertyManagementContextFacade) {
        this.propertyManagementContextFacade = propertyManagementContextFacade;
    }

    public List<Long> fetchResourceIdsOfAdministrator(Long administratorId) {
        return propertyManagementContextFacade.fetchGalleryIdByAdministratorId(administratorId)
                .map(propertyManagementContextFacade::fetchResourceIdsByGalleryId)
                .orElseGet(List::of);
    }

    public boolean isResourceOfAdministrator(Long resourceId, Long administratorId) {
        return fetchResourceIdsOfAdministrator(administratorId).contains(resourceId);
    }
}