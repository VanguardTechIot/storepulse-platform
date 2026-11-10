package com.vanguardtech.storepulse.platform.utilitybilling.interfaces.rest.resources;

import jakarta.validation.constraints.Size;

public record ReviewBillingDisputeResource(@Size(max = 1000) String reviewNotes) {
}