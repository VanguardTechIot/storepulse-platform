package com.vanguardtech.storepulse.platform.propertymanagement.interfaces.rest.resources;

/**
 * Partial update of a unit: a missing field keeps its value. {@code active=false} removes the unit
 * logically; {@code status} asks for AVAILABLE, OCCUPIED or MAINTENANCE.
 */
public record UpdateCommercialUnitResource(String code, String floor, Double areaSquareMeters, String businessName,
                                           Long resourceId, String status, Boolean active) {
}