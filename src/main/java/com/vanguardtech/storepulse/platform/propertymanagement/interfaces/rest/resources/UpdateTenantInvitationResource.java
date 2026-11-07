package com.vanguardtech.storepulse.platform.propertymanagement.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;

/** {@code status} is REVOKED (the administrator cancels the link) or ACCEPTED (the tenant used it). */
public record UpdateTenantInvitationResource(@NotBlank String status) {
}
