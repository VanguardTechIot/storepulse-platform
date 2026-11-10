package com.vanguardtech.storepulse.platform.propertymanagement.domain.model.commands;

/** Sends an invitation link to the email of the tenant of a store of the administrator. */
public record InviteTenantCommand(Long unitId, Long administratorId, String email) {
}