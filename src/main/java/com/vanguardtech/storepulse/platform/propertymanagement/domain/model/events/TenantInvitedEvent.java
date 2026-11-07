package com.vanguardtech.storepulse.platform.propertymanagement.domain.model.events;

import com.vanguardtech.storepulse.platform.shared.domain.model.events.DomainEvent;
import java.time.Instant;

/**
 * A tenant was invited to occupy a store (US-12). Property Communication uses it to deliver the
 * invitation link.
 *
 * @param invitationId identifier of the invitation
 * @param galleryId    gallery of the store
 * @param unitId       store the tenant is invited to
 * @param email        email the invitation is addressed to
 * @param token        token of the invitation link
 * @param occurredOn   moment the invitation was issued
 */
public record TenantInvitedEvent(Long invitationId, Long galleryId, Long unitId, String email, String token,
                                 Instant occurredOn) implements DomainEvent {
}
