package com.vanguardtech.storepulse.platform.utilitybilling.domain.model.commands;

/** Closes a dispute over a bill of the administrator with their resolution. */
public record ResolveBillingDisputeCommand(Long disputeId, Long administratorId, String resolution) {
}