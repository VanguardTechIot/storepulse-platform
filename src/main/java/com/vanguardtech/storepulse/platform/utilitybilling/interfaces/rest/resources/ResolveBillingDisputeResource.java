package com.vanguardtech.storepulse.platform.utilitybilling.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResolveBillingDisputeResource(@NotBlank @Size(max = 1000) String resolution) {
}