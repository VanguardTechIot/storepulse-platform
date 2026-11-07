package com.vanguardtech.storepulse.platform.propertymanagement.domain.model.commands;

/** Sends an invitation link to the email of the tenant of a store (US-12). */
public record InviteTenantCommand(Long unitId, String email) {
}
