package com.vanguardtech.storepulse.platform.propertymanagement.domain.model.commands;

import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.valueobjects.UnitStatus;

/**
 * Edits a unit of the administrator's gallery. Every field is optional: a {@code null} keeps the
 * current value, which is what a {@code PATCH} means.
 */
public record UpdateCommercialUnitCommand(Long unitId, Long administratorId, String code, String floor,
                                          Double areaSquareMeters, String businessName, Long resourceId,
                                          UnitStatus status) {
}