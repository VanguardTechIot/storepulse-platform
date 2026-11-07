package com.vanguardtech.storepulse.platform.propertymanagement.domain.model.commands;

import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.valueobjects.UnitType;

/** Registers a store or a common area in a gallery (US-09, US-57). */
public record RegisterCommercialUnitCommand(Long galleryId, UnitType type, String code, String floor,
                                            Double areaSquareMeters, String businessName) {
}
