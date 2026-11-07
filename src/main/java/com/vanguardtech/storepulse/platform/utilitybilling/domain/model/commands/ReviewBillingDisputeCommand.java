package com.vanguardtech.storepulse.platform.utilitybilling.domain.model.commands;

/** Moves an open dispute to review. The review notes are optional. */
public record ReviewBillingDisputeCommand(Long disputeId, String reviewNotes) {
}