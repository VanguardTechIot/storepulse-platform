package com.vanguardtech.storepulse.platform.propertycommunication.application.internal.commandservices;

import com.vanguardtech.storepulse.platform.propertycommunication.application.internal.outboundservices.acl.ExternalIamService;
import com.vanguardtech.storepulse.platform.propertycommunication.application.internal.outboundservices.acl.ExternalPropertyManagementService;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.exceptions.PropertyCommunicationError;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.aggregates.TenantAssignment;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.commands.AssignTenantCommand;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.commands.NotifyTenantCommand;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.commands.TerminateTenantAssignmentCommand;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.valueobjects.AssignmentStatus;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.valueobjects.NotificationChannel;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.valueobjects.NotificationType;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.services.TenantAssignmentCommandService;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.services.TenantNotificationCommandService;
import com.vanguardtech.storepulse.platform.propertycommunication.infrastructure.persistence.jpa.repositories.TenantAssignmentRepository;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TenantAssignmentCommandServiceImpl implements TenantAssignmentCommandService {

    private final TenantAssignmentRepository assignmentRepository;
    private final ExternalIamService externalIamService;
    private final ExternalPropertyManagementService externalPropertyManagementService;
    private final TenantNotificationCommandService notificationCommandService;

    public TenantAssignmentCommandServiceImpl(TenantAssignmentRepository assignmentRepository,
                                              ExternalIamService externalIamService,
                                              ExternalPropertyManagementService externalPropertyManagementService,
                                              TenantNotificationCommandService notificationCommandService) {
        this.assignmentRepository = assignmentRepository;
        this.externalIamService = externalIamService;
        this.externalPropertyManagementService = externalPropertyManagementService;
        this.notificationCommandService = notificationCommandService;
    }

    /** A store holds one active tenant; Property Management marks it occupied in the same transaction. */
    @Override
    @Transactional
    public TenantAssignment handle(AssignTenantCommand command) {
        if (!externalIamService.existsUser(command.tenantId()))
            throw new ResourceNotFoundException(PropertyCommunicationError.TENANT_NOT_FOUND);
        var unit = externalPropertyManagementService.fetchUnit(command.commercialUnitId())
                .orElseThrow(() -> new ResourceNotFoundException(PropertyCommunicationError.UNIT_NOT_FOUND));
        if (assignmentRepository.findByCommercialUnitIdAndStatus(unit.id(), AssignmentStatus.ACTIVE).isPresent())
            throw new BusinessRuleException(PropertyCommunicationError.UNIT_ALREADY_ASSIGNED);
        if (!unit.store() || !unit.available() || !unit.active())
            throw new BusinessRuleException(PropertyCommunicationError.UNIT_NOT_AVAILABLE);

        var assignment = assignmentRepository.save(new TenantAssignment(command.tenantId(), unit.id(),
                command.galleryAdministratorId()));
        externalPropertyManagementService.markUnitOccupied(unit.id());
        assignment.confirmAssigned();
        var saved = assignmentRepository.save(assignment);
        notificationCommandService.handle(new NotifyTenantCommand(saved.getTenantId(), saved.getCommercialUnitId(),
                saved.getGalleryAdministratorId(), NotificationType.ASSIGNMENT_CREATED, "Store " + unit.code() + " assigned",
                "You were assigned to store " + unit.code() + " on floor " + unit.floor() + ".",
                NotificationChannel.IN_APP, saved.getId()));
        return saved;
    }

    @Override
    @Transactional
    public TenantAssignment handle(TerminateTenantAssignmentCommand command) {
        var assignment = assignmentRepository.findById(command.assignmentId())
                .orElseThrow(() -> new ResourceNotFoundException(PropertyCommunicationError.ASSIGNMENT_NOT_FOUND));
        assignment.terminate();
        var saved = assignmentRepository.save(assignment);
        externalPropertyManagementService.markUnitAvailable(saved.getCommercialUnitId());
        return saved;
    }
}
