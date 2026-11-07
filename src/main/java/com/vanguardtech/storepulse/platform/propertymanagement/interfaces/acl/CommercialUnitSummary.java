package com.vanguardtech.storepulse.platform.propertymanagement.interfaces.acl;

/**
 * What other contexts may know about a unit: enough to name it and to tell whether it can
 * receive a tenant. It carries no entity of this context.
 */
public record CommercialUnitSummary(Long id, Long galleryId, String code, String floor, String businessName,
                                    boolean store, boolean available, boolean active) {
}
