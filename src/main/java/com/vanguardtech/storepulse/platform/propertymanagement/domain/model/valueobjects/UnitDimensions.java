package com.vanguardtech.storepulse.platform.propertymanagement.domain.model.valueobjects;

import com.vanguardtech.storepulse.platform.propertymanagement.domain.exceptions.PropertyManagementError;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException;
import jakarta.persistence.Embeddable;

/** Area of a unit in square meters, kept with two decimals. */
@Embeddable
public record UnitDimensions(Double areaSquareMeters) {

    public static final double MAX_AREA = 10_000;

    public UnitDimensions {
        if (!isValid(areaSquareMeters))
            throw new BusinessRuleException(PropertyManagementError.INVALID_AREA);
        areaSquareMeters = Math.round(areaSquareMeters * 100) / 100.0;
    }

    public static boolean isValid(Double areaSquareMeters) {
        return areaSquareMeters != null && areaSquareMeters > 0 && areaSquareMeters <= MAX_AREA;
    }
}
