package com.vanguardtech.storepulse.platform.monitoring.domain.model.valueobjects;

import java.time.Instant;

public record SensorStatus(MeasurementType type, Double value, Instant recordedAt) {
}
