package com.vanguardtech.storepulse.platform.propertymanagement.domain.model.commands;

/** Invalidates a pending invitation of the administrator's gallery. */
public record RevokeTenantInvitationCommand(Long invitationId, Long administratorId) {
}