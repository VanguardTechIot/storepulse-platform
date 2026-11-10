package com.vanguardtech.storepulse.platform.utilitybilling.domain.model.commands;

import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.valueobjects.DisputeReason;

/** The authenticated tenant raises a dispute over an issued bill of the store they occupy. */
public record RaiseBillingDisputeCommand(Long utilityBillId, DisputeReason reason, String description) {
}