package com.vanguardtech.storepulse.platform.propertycommunication.domain.model.commands;

/** Ends an assignment; the store becomes available again. */
public record TerminateTenantAssignmentCommand(Long assignmentId) {
}
