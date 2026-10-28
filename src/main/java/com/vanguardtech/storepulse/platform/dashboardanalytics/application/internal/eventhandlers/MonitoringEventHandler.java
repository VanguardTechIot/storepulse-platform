package com.vanguardtech.storepulse.platform.dashboardanalytics.application.internal.eventhandlers;

import com.vanguardtech.storepulse.platform.dashboardanalytics.domain.model.commands.GenerateNotificationCommand;
import com.vanguardtech.storepulse.platform.dashboardanalytics.domain.model.valueobjects.NotificationType;
import com.vanguardtech.storepulse.platform.dashboardanalytics.domain.services.NotificationCommandService;
import com.vanguardtech.storepulse.platform.monitoring.domain.model.events.MotionDetectedEvent;
import com.vanguardtech.storepulse.platform.monitoring.domain.model.events.SmokeDetectedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/** Turns the critical events of Service Execution and Monitoring into notifications. */
@Service
@RequiredArgsConstructor
public class MonitoringEventHandler {

    private final NotificationCommandService notificationCommandService;

    @EventListener
    public void on(MotionDetectedEvent event) {
        notificationCommandService.handle(new GenerateNotificationCommand(NotificationType.SAFETY_ALERT,
                event.deviceId(), "Motion detected"));
    }

    @EventListener
    public void on(SmokeDetectedEvent event) {
        notificationCommandService.handle(new GenerateNotificationCommand(NotificationType.SAFETY_ALERT,
                event.deviceId(), "Smoke detected: %.0f ppm".formatted(event.concentration())));
    }
}