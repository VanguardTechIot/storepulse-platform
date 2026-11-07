package com.vanguardtech.storepulse.platform.propertymanagement.interfaces.rest.transform;

import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.commands.RegisterCommercialGalleryCommand;
import com.vanguardtech.storepulse.platform.propertymanagement.interfaces.rest.resources.RegisterCommercialGalleryResource;

public class RegisterCommercialGalleryCommandFromResourceAssembler {
    public static RegisterCommercialGalleryCommand toCommandFromResource(Long administratorId,
                                                                         RegisterCommercialGalleryResource resource) {
        return new RegisterCommercialGalleryCommand(administratorId, resource.name(), resource.address(),
                resource.totalUnits());
    }
}
