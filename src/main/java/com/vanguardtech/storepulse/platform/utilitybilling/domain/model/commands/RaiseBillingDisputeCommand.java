package com.vanguardtech.storepulse.platform.utilitybilling.domain.model.commands;

import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.valueobjects.DisputeReason;

/** A tenant raises a dispute over an issued bill. */
public record RaiseBillingDisputeCommand(Long utilityBillId, Long tenantId, DisputeReason reason,
                                         String description) {
}