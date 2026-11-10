package com.vanguardtech.storepulse.platform.propertycommunication.domain.services;

import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.aggregates.TenantNotification;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.commands.MarkNotificationReadCommand;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.commands.NotifyTenantCommand;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.commands.PublishAnnouncementCommand;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.commands.RetryNotificationCommand;
import java.util.List;

public interface TenantNotificationCommandService {
    TenantNotification handle(NotifyTenantCommand command);
    List<TenantNotification> handle(PublishAnnouncementCommand command);
    TenantNotification handle(RetryNotificationCommand command);
    TenantNotification handle(MarkNotificationReadCommand command);
}
