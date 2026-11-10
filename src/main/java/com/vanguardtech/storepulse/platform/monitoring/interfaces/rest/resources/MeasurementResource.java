package com.vanguardtech.storepulse.platform.monitoring.interfaces.rest.resources;

import com.vanguardtech.storepulse.platform.monitoring.domain.model.valueobjects.MeasurementType;

import java.time.Instant;

public record MeasurementResource(Long id, MeasurementType type, Double value, String unit, Instant recordedAt) {
}
