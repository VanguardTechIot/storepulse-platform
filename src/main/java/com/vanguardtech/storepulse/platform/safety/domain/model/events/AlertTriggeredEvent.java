package com.vanguardtech.storepulse.platform.safety.domain.model.events;

import com.vanguardtech.storepulse.platform.shared.domain.model.events.DomainEvent;

import java.time.Instant;

public record AlertTriggeredEvent(Long deviceId, Long safetyRuleId, Double measuredValue, Instant occurredOn)
        implements DomainEvent {
}
