package com.vanguardtech.storepulse.platform.propertymanagement.domain.model.events;

import com.vanguardtech.storepulse.platform.shared.domain.model.events.DomainEvent;
import java.time.Instant;

/**
 * The invited tenant accepted the link. Property Communication assigns the tenant to the store,
 * which is what marks the store as occupied.
 */
public record TenantInvitationAcceptedEvent(Long invitationId, Long galleryId, Long unitId, Long tenantId,
                                            Instant occurredOn) implements DomainEvent {
}