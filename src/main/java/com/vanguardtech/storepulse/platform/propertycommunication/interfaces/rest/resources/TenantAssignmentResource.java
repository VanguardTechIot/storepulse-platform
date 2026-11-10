package com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.resources;

import java.time.Instant;

public record TenantAssignmentResource(Long id, Long tenantId, String tenantFullName, String tenantEmail,
                                       Long commercialUnitId, Long galleryAdministratorId, String status,
                                       Instant assignedAt, Instant terminatedAt) {
}