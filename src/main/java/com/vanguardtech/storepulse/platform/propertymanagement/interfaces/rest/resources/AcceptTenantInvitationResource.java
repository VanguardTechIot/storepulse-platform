package com.vanguardtech.storepulse.platform.propertymanagement.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;

/** Token of the invitation link; the tenant is the authenticated user. */
public record AcceptTenantInvitationResource(@NotBlank String token) {
}