package com.vanguardtech.storepulse.platform.utilitybilling.interfaces.rest.resources;

import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.valueobjects.DisputeReason;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record RaiseBillingDisputeResource(@NotNull @Positive Long utilityBillId, @NotNull DisputeReason reason,
                                          @Size(max = 1000) String description) {
}