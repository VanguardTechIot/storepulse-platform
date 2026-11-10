package com.vanguardtech.storepulse.platform.propertycommunication.domain.model.commands;

/** Assigns a registered tenant to a free store of the gallery. */
public record AssignTenantCommand(Long tenantId, Long commercialUnitId, Long galleryAdministratorId) {
}
