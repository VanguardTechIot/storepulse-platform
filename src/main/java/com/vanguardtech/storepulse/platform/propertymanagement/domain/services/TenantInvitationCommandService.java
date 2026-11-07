package com.vanguardtech.storepulse.platform.propertymanagement.domain.services;

import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.aggregates.TenantInvitation;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.commands.AcceptTenantInvitationCommand;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.commands.InviteTenantCommand;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.commands.RevokeTenantInvitationCommand;

public interface TenantInvitationCommandService {
    TenantInvitation handle(InviteTenantCommand command);
    TenantInvitation handle(RevokeTenantInvitationCommand command);
    TenantInvitation handle(AcceptTenantInvitationCommand command);
}
