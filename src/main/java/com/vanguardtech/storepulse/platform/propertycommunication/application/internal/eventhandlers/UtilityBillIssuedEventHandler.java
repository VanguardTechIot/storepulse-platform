package com.vanguardtech.storepulse.platform.propertycommunication.application.internal.eventhandlers;

import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.commands.NotifyTenantCommand;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.queries.GetActiveAssignmentByUnitIdQuery;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.valueobjects.NotificationChannel;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.valueobjects.NotificationType;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.services.TenantAssignmentQueryService;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.services.TenantNotificationCommandService;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.events.UtilityBillIssuedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Notifies the tenant of a store when Utility Billing issues its bill (pivotal event
 * {@code UtilityBillIssued}): the tenant learns about the bill without the administrator doing
 * anything else. Stores without an active tenant are skipped.
 */
@Service
public class UtilityBillIssuedEventHandler {

    private final TenantAssignmentQueryService assignmentQueryService;
    private final TenantNotificationCommandService notificationCommandService;

    public UtilityBillIssuedEventHandler(TenantAssignmentQueryService assignmentQueryService,
                                         TenantNotificationCommandService notificationCommandService) {
        this.assignmentQueryService = assignmentQueryService;
        this.notificationCommandService = notificationCommandService;
    }

    @EventListener
    public void on(UtilityBillIssuedEvent event) {
        assignmentQueryService.handle(new GetActiveAssignmentByUnitIdQuery(event.unitId()))
                .ifPresent(assignment -> notificationCommandService.handle(new NotifyTenantCommand(
                        assignment.getTenantId(), assignment.getCommercialUnitId(), assignment.getGalleryAdministratorId(),
                        NotificationType.UTILITY_BILL_ISSUED, "Your utility bill is ready",
                        "The utility bill of your store was issued. Open it to review the consumption behind it.",
                        NotificationChannel.IN_APP, event.utilityBillId().value())));
    }
}
