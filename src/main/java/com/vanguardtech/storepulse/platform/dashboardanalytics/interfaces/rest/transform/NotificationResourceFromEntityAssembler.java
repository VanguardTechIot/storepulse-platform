package com.vanguardtech.storepulse.platform.dashboardanalytics.interfaces.rest.transform;

import com.vanguardtech.storepulse.platform.dashboardanalytics.domain.model.aggregates.Notification;
import com.vanguardtech.storepulse.platform.dashboardanalytics.interfaces.rest.resources.NotificationResource;

public class NotificationResourceFromEntityAssembler {

    public static NotificationResource toResourceFromEntity(Notification notification) {
        return new NotificationResource(notification.getId(), notification.getType().name(),
                notification.getDeviceId(), notification.getResourceId(), notification.getMessage(),
                notification.getCreatedAt(), notification.isRead());
    }
}