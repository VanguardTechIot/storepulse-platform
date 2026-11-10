package com.vanguardtech.storepulse.platform.monitoring.domain.model.events;

import com.vanguardtech.storepulse.platform.shared.domain.model.events.DomainEvent;

import java.time.Instant;

public record MotionDetectedEvent(Long deviceId, Instant occurredOn) implements DomainEvent {
}
