package com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;

/** {@code tenantId} is required when an administrator starts it; a tenant leaves it out. */
public record StartConversationResource(Long tenantId, @NotBlank String subject) {
}