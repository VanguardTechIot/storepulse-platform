package com.vanguardtech.storepulse.platform.propertycommunication.domain.model.commands;

/** Ends an assignment of the administrator; the store becomes available again. */
public record TerminateTenantAssignmentCommand(Long assignmentId, Long administratorId) {
}