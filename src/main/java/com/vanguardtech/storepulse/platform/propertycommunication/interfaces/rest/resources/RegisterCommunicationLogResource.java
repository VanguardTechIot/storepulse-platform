package com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterCommunicationLogResource(@NotNull Long referenceId, @NotBlank String referenceType, @NotBlank String action,
                                               @NotNull Long tenantId, Long galleryAdministratorId, String channel, String detail) {
}
