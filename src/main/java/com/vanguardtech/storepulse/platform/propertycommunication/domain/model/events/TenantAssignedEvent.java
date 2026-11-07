package com.vanguardtech.storepulse.platform.propertycommunication.domain.model.events;

import com.vanguardtech.storepulse.platform.shared.domain.model.events.DomainEvent;
import java.time.Instant;

/** A tenant started occupying a store. */
public record TenantAssignedEvent(Long assignmentId, Long tenantId, Long commercialUnitId, Instant occurredOn)
        implements DomainEvent {
}
