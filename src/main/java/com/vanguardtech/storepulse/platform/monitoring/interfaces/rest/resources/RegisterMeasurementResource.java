package com.vanguardtech.storepulse.platform.monitoring.interfaces.rest.resources;

import com.vanguardtech.storepulse.platform.monitoring.domain.model.valueobjects.MeasurementType;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record RegisterMeasurementResource(@NotNull MeasurementType type, @NotNull Double value, Instant recordedAt) {
}
