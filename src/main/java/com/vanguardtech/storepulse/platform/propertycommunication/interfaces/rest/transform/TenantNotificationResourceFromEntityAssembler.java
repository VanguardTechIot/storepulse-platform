package com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.transform;

import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.aggregates.TenantNotification;
import com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.resources.TenantNotificationResource;

public class TenantNotificationResourceFromEntityAssembler {
    public static TenantNotificationResource toResourceFromEntity(TenantNotification notification) {
        return new TenantNotificationResource(notification.getId(), notification.getTenantId(), notification.getCommercialUnitId(),
                notification.getGalleryAdministratorId(), notification.getType().name(), notification.getTitle(),
                notification.getBody(), notification.getChannel().name(), notification.getStatus().name(),
                notification.isRead(), notification.getCreatedAt(), notification.getReferenceId());
    }
}
