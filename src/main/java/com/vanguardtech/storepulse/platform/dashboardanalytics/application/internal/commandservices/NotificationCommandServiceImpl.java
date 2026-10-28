package com.vanguardtech.storepulse.platform.dashboardanalytics.application.internal.commandservices;

import com.vanguardtech.storepulse.platform.dashboardanalytics.application.internal.outboundservices.acl.ExternalAssetsService;
import com.vanguardtech.storepulse.platform.dashboardanalytics.domain.exceptions.AnalyticsError;
import com.vanguardtech.storepulse.platform.dashboardanalytics.domain.model.aggregates.Notification;
import com.vanguardtech.storepulse.platform.dashboardanalytics.domain.model.commands.GenerateNotificationCommand;
import com.vanguardtech.storepulse.platform.dashboardanalytics.domain.model.commands.MarkAllNotificationsAsReadCommand;
import com.vanguardtech.storepulse.platform.dashboardanalytics.domain.model.commands.MarkNotificationAsReadCommand;
import com.vanguardtech.storepulse.platform.dashboardanalytics.domain.services.NotificationCommandService;
import com.vanguardtech.storepulse.platform.dashboardanalytics.infrastructure.persistence.jpa.repositories.NotificationRepository;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationCommandServiceImpl implements NotificationCommandService {

    private final NotificationRepository notificationRepository;
    private final ExternalAssetsService externalAssetsService;

    @Override
    public Notification handle(GenerateNotificationCommand command) {
        var resourceId = externalAssetsService.fetchResourceIdByDeviceId(command.deviceId()).orElse(null);
        return notificationRepository.save(new Notification(command.type(), command.deviceId(), resourceId,
                command.message()));
    }

    @Override
    public Notification handle(MarkNotificationAsReadCommand command) {
        var notification = notificationRepository.findById(command.notificationId())
                .orElseThrow(() -> new ResourceNotFoundException(AnalyticsError.NOTIFICATION_NOT_FOUND));
        notification.markAsRead();
        return notificationRepository.save(notification);
    }

    @Override
    public void handle(MarkAllNotificationsAsReadCommand command) {
        notificationRepository.markAllAsRead();
    }
}