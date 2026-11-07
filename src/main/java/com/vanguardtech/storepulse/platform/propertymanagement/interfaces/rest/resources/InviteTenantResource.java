package com.vanguardtech.storepulse.platform.propertymanagement.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;

public record InviteTenantResource(@NotBlank String email) {
}
