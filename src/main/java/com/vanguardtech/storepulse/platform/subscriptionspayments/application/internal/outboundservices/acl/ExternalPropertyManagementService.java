package com.vanguardtech.storepulse.platform.subscriptionspayments.application.internal.outboundservices.acl;

import com.vanguardtech.storepulse.platform.propertymanagement.interfaces.acl.PropertyManagementContextFacade;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.valueobjects.AdministratorId;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.valueobjects.GalleryId;
import org.springframework.stereotype.Service;

/** Reads from Property Management which administrator manages a gallery. */
@Service("subscriptionsExternalPropertyManagementService")
public class ExternalPropertyManagementService {

    private final PropertyManagementContextFacade propertyManagementContextFacade;

    public ExternalPropertyManagementService(PropertyManagementContextFacade propertyManagementContextFacade) {
        this.propertyManagementContextFacade = propertyManagementContextFacade;
    }

    public boolean isGalleryOfAdministrator(GalleryId galleryId, AdministratorId administratorId) {
        return propertyManagementContextFacade.fetchAdministratorIdByGalleryId(galleryId.value())
                .map(administratorId.value()::equals)
                .orElse(false);
    }
}