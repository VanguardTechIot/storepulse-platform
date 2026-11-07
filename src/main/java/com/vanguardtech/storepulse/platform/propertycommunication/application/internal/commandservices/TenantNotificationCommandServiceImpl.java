package com.vanguardtech.storepulse.platform.propertycommunication.application.internal.commandservices;

import com.vanguardtech.storepulse.platform.propertycommunication.application.internal.outboundservices.NotificationDispatcher;
import com.vanguardtech.storepulse.platform.propertycommunication.application.internal.outboundservices.acl.ExternalPropertyManagementService;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.exceptions.PropertyCommunicationError;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.aggregates.TenantNotification;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.commands.MarkNotificationReadCommand;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.commands.NotifyTenantCommand;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.commands.PublishAnnouncementCommand;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.commands.RegisterCommunicationLogCommand;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.commands.RetryNotificationCommand;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.valueobjects.AssignmentStatus;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.valueobjects.CommunicationAction;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.valueobjects.ReferenceType;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.services.CommunicationLogCommandService;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.services.TenantNotificationCommandService;
import com.vanguardtech.storepulse.platform.propertycommunication.infrastructure.persistence.jpa.repositories.TenantAssignmentRepository;
import com.vanguardtech.storepulse.platform.propertycommunication.infrastructure.persistence.jpa.repositories.TenantNotificationRepository;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class TenantNotificationCommandServiceImpl implements TenantNotificationCommandService {

    private final TenantNotificationRepository notificationRepository;
    private final TenantAssignmentRepository assignmentRepository;
    private final NotificationDispatcher dispatcher;
    private final CommunicationLogCommandService logCommandService;
    private final ExternalPropertyManagementService externalPropertyManagementService;

    public TenantNotificationCommandServiceImpl(TenantNotificationRepository notificationRepository,
                                                TenantAssignmentRepository assignmentRepository,
                                                NotificationDispatcher dispatcher,
                                                CommunicationLogCommandService logCommandService,
                                                ExternalPropertyManagementService externalPropertyManagementService) {
        this.notificationRepository = notificationRepository;
        this.assignmentRepository = assignmentRepository;
        this.dispatcher = dispatcher;
        this.logCommandService = logCommandService;
        this.externalPropertyManagementService = externalPropertyManagementService;
    }

    @Override
    @Transactional
    public TenantNotification handle(NotifyTenantCommand command) {
        var notification = notificationRepository.save(new TenantNotification(command.tenantId(), command.commercialUnitId(),
                command.galleryAdministratorId(), command.type(), command.title(), command.body(), command.channel(),
                command.referenceId()));
        return deliver(notification);
    }

    /** US-34, US-35: one notification per tenant currently assigned to the chosen stores. */
    @Override
    @Transactional
    public List<TenantNotification> handle(PublishAnnouncementCommand command) {
        var unitIds = command.wholeGallery()
                ? externalPropertyManagementService.fetchStoreIdsByAdministratorId(command.galleryAdministratorId())
                : command.unitIds();
        var recipients = assignmentRepository
                .findByGalleryAdministratorIdAndStatus(command.galleryAdministratorId(), AssignmentStatus.ACTIVE).stream()
                .filter(assignment -> unitIds.contains(assignment.getCommercialUnitId()))
                .toList();
        if (recipients.isEmpty())
            throw new BusinessRuleException(PropertyCommunicationError.NO_RECIPIENTS);
        return recipients.stream()
                .map(assignment -> handle(new NotifyTenantCommand(assignment.getTenantId(), assignment.getCommercialUnitId(),
                        command.galleryAdministratorId(), command.type(), command.title(), command.body(),
                        command.channel(), null)))
                .toList();
    }

    @Override
    @Transactional
    public TenantNotification handle(RetryNotificationCommand command) {
        var notification = findNotification(command.notificationId());
        notification.retry();
        return deliver(notificationRepository.save(notification));
    }

    @Override
    @Transactional
    public TenantNotification handle(MarkNotificationReadCommand command) {
        var notification = findNotification(command.notificationId());
        notification.markRead();
        return notificationRepository.save(notification);
    }

    private TenantNotification deliver(TenantNotification notification) {
        var delivered = dispatcher.dispatch(notification);
        if (delivered)
            notification.markSent();
        else
            notification.markFailed();
        var saved = notificationRepository.save(notification);
        logCommandService.handle(new RegisterCommunicationLogCommand(saved.getId(), ReferenceType.NOTIFICATION,
                delivered ? CommunicationAction.NOTIFICATION_SENT : CommunicationAction.NOTIFICATION_FAILED,
                saved.getTenantId(), saved.getGalleryAdministratorId(), saved.getChannel(), saved.getTitle()));
        return saved;
    }

    private TenantNotification findNotification(Long notificationId) {
        return notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException(PropertyCommunicationError.NOTIFICATION_NOT_FOUND));
    }
}
