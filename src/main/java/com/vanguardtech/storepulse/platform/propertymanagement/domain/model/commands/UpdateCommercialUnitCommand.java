package com.vanguardtech.storepulse.platform.propertymanagement.domain.model.commands;

import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.valueobjects.UnitStatus;

/**
 * Edits a unit (US-10). Every field is optional: a {@code null} keeps the current value, which is
 * what a {@code PATCH} means.
 */
public record UpdateCommercialUnitCommand(Long unitId, String code, String floor, Double areaSquareMeters,
                                          String businessName, UnitStatus status) {
}
