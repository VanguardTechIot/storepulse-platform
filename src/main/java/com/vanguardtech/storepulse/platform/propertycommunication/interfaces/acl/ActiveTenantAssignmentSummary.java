package com.vanguardtech.storepulse.platform.propertycommunication.interfaces.acl;

/** Active assignment of a tenant, as other contexts see it. */
public record ActiveTenantAssignmentSummary(Long assignmentId, Long tenantId, Long commercialUnitId, Long galleryAdministratorId) {
}
