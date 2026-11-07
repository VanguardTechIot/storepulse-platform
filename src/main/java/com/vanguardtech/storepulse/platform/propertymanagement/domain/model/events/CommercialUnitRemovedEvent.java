package com.vanguardtech.storepulse.platform.propertymanagement.domain.model.events;

import com.vanguardtech.storepulse.platform.shared.domain.model.events.DomainEvent;
import java.time.Instant;

/** A unit was logically removed from the gallery (US-11); it no longer receives tenants or devices. */
public record CommercialUnitRemovedEvent(Long unitId, Long galleryId, Instant occurredOn) implements DomainEvent {
}
