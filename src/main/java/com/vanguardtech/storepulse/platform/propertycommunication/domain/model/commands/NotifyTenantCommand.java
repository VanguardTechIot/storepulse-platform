package com.vanguardtech.storepulse.platform.propertycommunication.domain.model.commands;

import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.valueobjects.NotificationChannel;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.valueobjects.NotificationType;

/** Notifies a tenant about something relevant to them; the platform delivers it right away. */
public record NotifyTenantCommand(Long tenantId, Long commercialUnitId, Long galleryAdministratorId, NotificationType type,
                                  String title, String body, NotificationChannel channel, Long referenceId) {
}
