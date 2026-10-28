package com.vanguardtech.storepulse.platform.dashboardanalytics.domain.model.commands;

import com.vanguardtech.storepulse.platform.dashboardanalytics.domain.model.valueobjects.NotificationType;

public record GenerateNotificationCommand(NotificationType type, Long deviceId, String message) {
}