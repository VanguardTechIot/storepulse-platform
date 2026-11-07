package com.vanguardtech.storepulse.platform.utilitybilling.domain.model.commands;

/** Verifies the data of a draft bill. Incomplete data needs {@code acceptIncompleteData}. */
public record VerifyBillingDataCommand(Long utilityBillId, boolean acceptIncompleteData) {
}