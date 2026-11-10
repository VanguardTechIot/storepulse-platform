package com.vanguardtech.storepulse.platform.propertymanagement.interfaces.rest.transform;

import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.commands.UpdateCommercialUnitCommand;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.valueobjects.UnitStatus;
import com.vanguardtech.storepulse.platform.propertymanagement.interfaces.rest.resources.UpdateCommercialUnitResource;

public class UpdateCommercialUnitCommandFromResourceAssembler {
    public static UpdateCommercialUnitCommand toCommandFromResource(Long unitId, Long administratorId,
                                                                    UpdateCommercialUnitResource resource) {
        return new UpdateCommercialUnitCommand(unitId, administratorId, resource.code(), resource.floor(),
                resource.areaSquareMeters(), resource.businessName(), resource.resourceId(),
                parseStatus(resource.status()));
    }

    private static UnitStatus parseStatus(String status) {
        if (status == null || status.isBlank())
            return null;
        try {
            return UnitStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Unit status must be AVAILABLE, OCCUPIED or MAINTENANCE");
        }
    }
}