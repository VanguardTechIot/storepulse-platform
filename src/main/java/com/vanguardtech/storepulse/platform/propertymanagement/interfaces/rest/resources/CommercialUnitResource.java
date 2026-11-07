package com.vanguardtech.storepulse.platform.propertymanagement.interfaces.rest.resources;

import java.time.Instant;

public record CommercialUnitResource(Long id, Long galleryId, String type, String code, String floor,
                                     Double areaSquareMeters, String businessName, String status, boolean active,
                                     Instant createdAt) {
}
