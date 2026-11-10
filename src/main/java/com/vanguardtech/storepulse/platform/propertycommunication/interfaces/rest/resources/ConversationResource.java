package com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.resources;

import java.time.Instant;

public record ConversationResource(Long id, Long tenantId, Long commercialUnitId, Long galleryAdministratorId, String subject,
                                   String status, Instant startedAt, Instant lastMessageAt, Instant resolvedAt) {
}
