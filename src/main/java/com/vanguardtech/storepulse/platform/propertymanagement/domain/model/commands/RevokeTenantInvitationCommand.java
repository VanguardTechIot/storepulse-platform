package com.vanguardtech.storepulse.platform.propertymanagement.domain.model.commands;

/** Invalidates a pending invitation, for example before sending a new one. */
public record RevokeTenantInvitationCommand(Long invitationId) {
}
