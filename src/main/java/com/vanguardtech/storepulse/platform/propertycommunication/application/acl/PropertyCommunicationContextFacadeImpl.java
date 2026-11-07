package com.vanguardtech.storepulse.platform.propertycommunication.application.acl;

import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.aggregates.TenantAssignment;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.commands.NotifyTenantCommand;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.queries.GetActiveAssignmentByUnitIdQuery;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.queries.GetAssignmentsByAdministratorIdQuery;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.valueobjects.NotificationChannel;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.valueobjects.NotificationType;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.services.TenantAssignmentQueryService;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.services.TenantNotificationCommandService;
import com.vanguardtech.storepulse.platform.propertycommunication.interfaces.acl.ActiveTenantAssignmentSummary;
import com.vanguardtech.storepulse.platform.propertycommunication.interfaces.acl.PropertyCommunicationContextFacade;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

/** Implementation of {@link PropertyCommunicationContextFacade} over the services of the context. */
@Service
public class PropertyCommunicationContextFacadeImpl implements PropertyCommunicationContextFacade {

    private final TenantAssignmentQueryService assignmentQueryService;
    private final TenantNotificationCommandService notificationCommandService;

    public PropertyCommunicationContextFacadeImpl(TenantAssignmentQueryService assignmentQueryService,
                                                  TenantNotificationCommandService notificationCommandService) {
        this.assignmentQueryService = assignmentQueryService;
        this.notificationCommandService = notificationCommandService;
    }

    @Override
    public Optional<Long> fetchActiveTenantIdByUnitId(Long commercialUnitId) {
        return assignmentQueryService.handle(new GetActiveAssignmentByUnitIdQuery(commercialUnitId))
                .map(TenantAssignment::getTenantId);
    }

    @Override
    public List<ActiveTenantAssignmentSummary> fetchActiveAssignmentsByAdministratorId(Long galleryAdministratorId) {
        return assignmentQueryService.handle(new GetAssignmentsByAdministratorIdQuery(galleryAdministratorId)).stream()
                .filter(TenantAssignment::isActive)
                .map(assignment -> new ActiveTenantAssignmentSummary(assignment.getId(), assignment.getTenantId(),
                        assignment.getCommercialUnitId(), assignment.getGalleryAdministratorId()))
                .toList();
    }

    @Override
    public Long notifyTenant(Long tenantId, Long commercialUnitId, Long galleryAdministratorId, String type, String title,
                             String body, Long referenceId) {
        var notification = notificationCommandService.handle(new NotifyTenantCommand(tenantId, commercialUnitId,
                galleryAdministratorId, NotificationType.valueOf(type), title, body, NotificationChannel.IN_APP, referenceId));
        return notification.getId();
    }
}
