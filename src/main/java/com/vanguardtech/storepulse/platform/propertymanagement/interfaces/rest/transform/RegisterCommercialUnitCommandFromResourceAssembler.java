package com.vanguardtech.storepulse.platform.propertymanagement.interfaces.rest.transform;

import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.commands.RegisterCommercialUnitCommand;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.valueobjects.UnitType;
import com.vanguardtech.storepulse.platform.propertymanagement.interfaces.rest.resources.RegisterCommercialUnitResource;

public class RegisterCommercialUnitCommandFromResourceAssembler {
    public static RegisterCommercialUnitCommand toCommandFromResource(Long galleryId, Long administratorId,
                                                                      RegisterCommercialUnitResource resource) {
        return new RegisterCommercialUnitCommand(galleryId, administratorId, parseType(resource.type()), resource.code(),
                resource.floor(), resource.areaSquareMeters(), resource.businessName(), resource.resourceId());
    }

    private static UnitType parseType(String type) {
        try {
            return UnitType.valueOf(type.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Unit type must be STORE or COMMON_AREA");
        }
    }
}