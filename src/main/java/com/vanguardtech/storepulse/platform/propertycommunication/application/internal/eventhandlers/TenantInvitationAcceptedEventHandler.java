package com.vanguardtech.storepulse.platform.propertycommunication.application.internal.eventhandlers;

import com.vanguardtech.storepulse.platform.propertycommunication.application.internal.outboundservices.acl.ExternalPropertyManagementService;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.exceptions.PropertyCommunicationError;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.commands.AssignTenantCommand;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.services.TenantAssignmentCommandService;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.events.TenantInvitationAcceptedEvent;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.ResourceNotFoundException;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Assigns the tenant to the store when they accept the invitation of Property Management. It runs
 * in the same transaction, so the acceptance is undone when the store can no longer be assigned.
 */
@Service
public class TenantInvitationAcceptedEventHandler {

    private final TenantAssignmentCommandService assignmentCommandService;
    private final ExternalPropertyManagementService externalPropertyManagementService;

    public TenantInvitationAcceptedEventHandler(TenantAssignmentCommandService assignmentCommandService,
                                                ExternalPropertyManagementService externalPropertyManagementService) {
        this.assignmentCommandService = assignmentCommandService;
        this.externalPropertyManagementService = externalPropertyManagementService;
    }

    @EventListener
    public void on(TenantInvitationAcceptedEvent event) {
        var administratorId = externalPropertyManagementService.fetchAdministratorIdByGalleryId(event.galleryId())
                .orElseThrow(() -> new ResourceNotFoundException(PropertyCommunicationError.UNIT_NOT_FOUND));
        assignmentCommandService.handle(new AssignTenantCommand(event.tenantId(), event.unitId(), administratorId));
    }
}