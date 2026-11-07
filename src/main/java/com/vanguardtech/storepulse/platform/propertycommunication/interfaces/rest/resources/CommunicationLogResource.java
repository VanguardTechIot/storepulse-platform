package com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.resources;

import java.time.Instant;

public record CommunicationLogResource(Long id, Long referenceId, String referenceType, String action, Long tenantId,
                                       Long galleryAdministratorId, String channel, String detail, Instant loggedAt) {
}
