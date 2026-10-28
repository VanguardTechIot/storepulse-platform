package com.vanguardtech.storepulse.platform.dashboardanalytics.interfaces.rest.resources;

import java.time.Instant;

public record NotificationResource(Long id, String type, Long deviceId, Long resourceId, String message,
                                   Instant generatedAt, boolean read) {
}