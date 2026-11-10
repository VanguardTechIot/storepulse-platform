package com.vanguardtech.storepulse.platform.propertymanagement.interfaces.rest.transform;

import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.aggregates.CommercialGallery;
import com.vanguardtech.storepulse.platform.propertymanagement.interfaces.rest.resources.CommercialGalleryResource;

public class CommercialGalleryResourceFromEntityAssembler {
    public static CommercialGalleryResource toResourceFromEntity(CommercialGallery gallery) {
        return new CommercialGalleryResource(gallery.getId(), gallery.getAdministratorId(), gallery.getName(),
                gallery.getAddress(), gallery.getTotalUnits(), gallery.getRegisteredAt());
    }
}
