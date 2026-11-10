package com.vanguardtech.storepulse.platform.monitoring.domain.model.events;

import com.vanguardtech.storepulse.platform.monitoring.domain.model.valueobjects.MeasurementType;
import com.vanguardtech.storepulse.platform.shared.domain.model.events.DomainEvent;

import java.time.Instant;

public record UtilityConsumptionRecordedEvent(Long deviceId, MeasurementType type, Double value, Instant occurredOn)
        implements DomainEvent {
}
