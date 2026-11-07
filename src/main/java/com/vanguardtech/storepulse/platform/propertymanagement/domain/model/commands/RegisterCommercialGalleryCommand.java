package com.vanguardtech.storepulse.platform.propertymanagement.domain.model.commands;

/** Registers the gallery of the authenticated administrator (US-08). */
public record RegisterCommercialGalleryCommand(Long administratorId, String name, String address, Integer totalUnits) {
}
