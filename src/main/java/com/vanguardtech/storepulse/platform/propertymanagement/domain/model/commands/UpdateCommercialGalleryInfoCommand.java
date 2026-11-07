package com.vanguardtech.storepulse.platform.propertymanagement.domain.model.commands;

/** Replaces the general data of the gallery. */
public record UpdateCommercialGalleryInfoCommand(Long galleryId, String name, String address, Integer totalUnits) {
}
