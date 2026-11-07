package com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** The administrator may be omitted: the authenticated administrator is used then. */
public record StartConversationResource(@NotNull Long tenantId, Long commercialUnitId, Long galleryAdministratorId,
                                        @NotBlank String subject) {
}
