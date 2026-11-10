package com.vanguardtech.storepulse.platform.propertymanagement.domain.model.commands;

import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.valueobjects.UnitType;

/**
 * Registers a store or a common area in the gallery managed by the administrator. {@code resourceId}
 * is the optional resource of Resource and Asset Management whose meters measure the unit.
 */
public record RegisterCommercialUnitCommand(Long galleryId, Long administratorId, UnitType type, String code,
                                            String floor, Double areaSquareMeters, String businessName,
                                            Long resourceId) {
}