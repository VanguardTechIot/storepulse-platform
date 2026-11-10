package com.vanguardtech.storepulse.platform.utilitybilling.domain.model.commands;

/** Moves an open dispute over a bill of the administrator to review. The review notes are optional. */
public record ReviewBillingDisputeCommand(Long disputeId, Long administratorId, String reviewNotes) {
}