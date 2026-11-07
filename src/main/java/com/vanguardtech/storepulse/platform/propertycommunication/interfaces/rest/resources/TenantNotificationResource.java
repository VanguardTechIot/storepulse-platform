package com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.resources;

import java.time.Instant;

public record TenantNotificationResource(Long id, Long tenantId, Long commercialUnitId, Long galleryAdministratorId, String type,
                                         String title, String body, String channel, String status, boolean read,
                                         Instant createdAt, Long referenceId) {
}
