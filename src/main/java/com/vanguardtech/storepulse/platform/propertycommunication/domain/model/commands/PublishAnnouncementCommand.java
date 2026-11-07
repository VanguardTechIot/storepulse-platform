package com.vanguardtech.storepulse.platform.propertycommunication.domain.model.commands;

import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.valueobjects.NotificationChannel;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.valueobjects.NotificationType;
import java.util.List;

/**
 * Publishes a communication of the administrator to the tenants of the given stores, or to every
 * tenant of the gallery when {@code wholeGallery} is set (US-34 announcement, US-35 incident).
 */
public record PublishAnnouncementCommand(Long galleryAdministratorId, NotificationType type, List<Long> unitIds,
                                         boolean wholeGallery, String title, String body, NotificationChannel channel) {
}
