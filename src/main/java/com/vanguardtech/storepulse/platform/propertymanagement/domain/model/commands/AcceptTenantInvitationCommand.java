package com.vanguardtech.storepulse.platform.propertymanagement.domain.model.commands;

/** The invited tenant registered with the link; the store becomes occupied. */
public record AcceptTenantInvitationCommand(Long invitationId) {
}
