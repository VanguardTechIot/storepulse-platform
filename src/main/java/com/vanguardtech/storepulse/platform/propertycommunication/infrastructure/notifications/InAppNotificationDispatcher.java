package com.vanguardtech.storepulse.platform.propertycommunication.infrastructure.notifications;

import com.vanguardtech.storepulse.platform.propertycommunication.application.internal.outboundservices.NotificationDispatcher;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.aggregates.TenantNotification;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.valueobjects.NotificationChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Default delivery: in-app notifications are stored and read by the applications, so they are
 * delivered as soon as they are saved. Email and push are logged until a provider is configured.
 */
@Component
public class InAppNotificationDispatcher implements NotificationDispatcher {

    private static final Logger log = LoggerFactory.getLogger(InAppNotificationDispatcher.class);

    @Override
    public boolean dispatch(TenantNotification notification) {
        if (notification.getChannel() != NotificationChannel.IN_APP)
            log.info("Notification {} for tenant {} queued on channel {}", notification.getId(),
                    notification.getTenantId(), notification.getChannel());
        return true;
    }
}
