package com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** A single notification to one tenant of the authenticated administrator. */
public record NotifyTenantResource(@NotNull Long tenantId, Long commercialUnitId, @NotBlank String type,
                                   @NotBlank String title, @NotBlank String body, String channel, Long referenceId) {
}