package com.vanguardtech.storepulse.platform.propertymanagement.interfaces.rest.resources;

import java.time.Instant;

public record CommercialGalleryResource(Long id, Long administratorId, String name, String address, Integer totalUnits,
                                        Instant registeredAt) {
}
