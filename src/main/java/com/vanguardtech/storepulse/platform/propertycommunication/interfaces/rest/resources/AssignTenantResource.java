package com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.resources;

import jakarta.validation.constraints.NotNull;

public record AssignTenantResource(@NotNull Long tenantId, @NotNull Long commercialUnitId, Long galleryAdministratorId) {
}
