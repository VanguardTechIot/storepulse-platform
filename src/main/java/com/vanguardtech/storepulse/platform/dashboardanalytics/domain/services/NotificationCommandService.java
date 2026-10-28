package com.vanguardtech.storepulse.platform.dashboardanalytics.domain.services;

import com.vanguardtech.storepulse.platform.dashboardanalytics.domain.model.aggregates.Notification;
import com.vanguardtech.storepulse.platform.dashboardanalytics.domain.model.commands.GenerateNotificationCommand;
import com.vanguardtech.storepulse.platform.dashboardanalytics.domain.model.commands.MarkAllNotificationsAsReadCommand;
import com.vanguardtech.storepulse.platform.dashboardanalytics.domain.model.commands.MarkNotificationAsReadCommand;

public interface NotificationCommandService {

    Notification handle(GenerateNotificationCommand command);

    Notification handle(MarkNotificationAsReadCommand command);

    void handle(MarkAllNotificationsAsReadCommand command);
}