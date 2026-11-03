package com.vanguardtech.storepulse.platform.safety.domain.model.events;

import com.vanguardtech.storepulse.platform.shared.domain.model.events.DomainEvent;

import java.time.Instant;

public record AlertAcknowledgedEvent(Long alertId, Instant occurredOn) implements DomainEvent {
}
