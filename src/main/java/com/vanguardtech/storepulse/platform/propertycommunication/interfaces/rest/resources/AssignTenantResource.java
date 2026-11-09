package com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.resources;

import jakarta.validation.constraints.NotNull;

/** The administrator is the authenticated user. */
public record AssignTenantResource(@NotNull Long tenantId, @NotNull Long commercialUnitId) {
}