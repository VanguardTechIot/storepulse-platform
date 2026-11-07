package com.vanguardtech.storepulse.platform.propertymanagement.interfaces.rest.transform;

import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.commands.UpdateCommercialGalleryInfoCommand;
import com.vanguardtech.storepulse.platform.propertymanagement.interfaces.rest.resources.UpdateCommercialGalleryResource;

public class UpdateCommercialGalleryInfoCommandFromResourceAssembler {
    public static UpdateCommercialGalleryInfoCommand toCommandFromResource(Long galleryId,
                                                                           UpdateCommercialGalleryResource resource) {
        return new UpdateCommercialGalleryInfoCommand(galleryId, resource.name(), resource.address(),
                resource.totalUnits());
    }
}
