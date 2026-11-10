package com.vanguardtech.storepulse.platform.propertycommunication.domain.model.events;

import com.vanguardtech.storepulse.platform.shared.domain.model.events.DomainEvent;
import java.time.Instant;

/** A tenant left a store, which becomes available again. */
public record TenantAssignmentTerminatedEvent(Long assignmentId, Long tenantId, Long commercialUnitId, Instant occurredOn)
        implements DomainEvent {
}
