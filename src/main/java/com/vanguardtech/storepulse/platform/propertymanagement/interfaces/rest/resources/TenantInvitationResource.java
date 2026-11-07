package com.vanguardtech.storepulse.platform.propertymanagement.interfaces.rest.resources;

import java.time.Instant;

public record TenantInvitationResource(Long id, Long unitId, Long galleryId, String email, String token, String status,
                                       Instant sentAt, Instant expiresAt) {
}
