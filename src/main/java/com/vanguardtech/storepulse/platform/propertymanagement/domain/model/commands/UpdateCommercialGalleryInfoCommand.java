package com.vanguardtech.storepulse.platform.propertymanagement.domain.model.commands;

/** Replaces the general data of the gallery managed by the administrator. */
public record UpdateCommercialGalleryInfoCommand(Long galleryId, Long administratorId, String name, String address,
                                                 Integer totalUnits) {
}