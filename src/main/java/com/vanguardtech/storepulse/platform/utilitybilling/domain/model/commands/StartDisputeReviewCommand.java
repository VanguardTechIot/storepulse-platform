package com.vanguardtech.storepulse.platform.utilitybilling.domain.model.commands;

/** Moves an open dispute to review. The review notes are optional. */
public record StartDisputeReviewCommand(Long disputeId, String reviewNotes) {
}