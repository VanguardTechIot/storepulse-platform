package com.vanguardtech.storepulse.platform.utilitybilling.domain.model.commands;

/** Closes a dispute with the administrator's resolution. */
public record ResolveDisputeCommand(Long disputeId, String resolution) {
}