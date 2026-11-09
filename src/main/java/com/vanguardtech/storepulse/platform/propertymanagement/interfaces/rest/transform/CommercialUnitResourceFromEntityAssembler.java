package com.vanguardtech.storepulse.platform.propertymanagement.interfaces.rest.transform;

import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.aggregates.CommercialUnit;
import com.vanguardtech.storepulse.platform.propertymanagement.interfaces.rest.resources.CommercialUnitResource;

public class CommercialUnitResourceFromEntityAssembler {
    public static CommercialUnitResource toResourceFromEntity(CommercialUnit unit) {
        return new CommercialUnitResource(unit.getId(), unit.getGalleryId(), unit.getType().name(), unit.getCode(),
                unit.getFloor(), unit.getAreaSquareMeters(), unit.getBusinessName(), unit.getResourceId(), unit.getStatus().name(),
                unit.isActive(), unit.getCreatedAt());
    }
}