package com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.transform;

import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.commands.NotifyTenantCommand;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.valueobjects.NotificationChannel;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.valueobjects.NotificationType;
import com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.resources.NotifyTenantResource;

public class NotifyTenantCommandFromResourceAssembler {
    public static NotifyTenantCommand toCommandFromResource(Long administratorId, NotifyTenantResource resource) {
        return new NotifyTenantCommand(resource.tenantId(), resource.commercialUnitId(), administratorId,
                Enums.parse(NotificationType.class, resource.type()), resource.title(), resource.body(),
                Enums.parseOrDefault(NotificationChannel.class, resource.channel(), NotificationChannel.IN_APP),
                resource.referenceId());
    }
}
