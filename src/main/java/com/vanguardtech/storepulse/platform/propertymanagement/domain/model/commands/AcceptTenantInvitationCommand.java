package com.vanguardtech.storepulse.platform.propertymanagement.domain.model.commands;

/** The tenant opened the invitation link while signed in; the token identifies the invitation. */
public record AcceptTenantInvitationCommand(String token, Long tenantId) {
}