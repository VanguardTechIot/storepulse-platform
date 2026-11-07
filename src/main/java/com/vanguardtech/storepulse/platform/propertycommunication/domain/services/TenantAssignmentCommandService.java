package com.vanguardtech.storepulse.platform.propertycommunication.domain.services;

import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.aggregates.TenantAssignment;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.commands.AssignTenantCommand;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.commands.TerminateTenantAssignmentCommand;

public interface TenantAssignmentCommandService {
    TenantAssignment handle(AssignTenantCommand command);
    TenantAssignment handle(TerminateTenantAssignmentCommand command);
}
