package com.vanguardtech.storepulse.platform.monitoring.domain.model.commands;

import com.vanguardtech.storepulse.platform.monitoring.domain.model.valueobjects.MeasurementType;

import java.time.Instant;

public record RegisterMeasurementCommand(MeasurementType type, Double value, Instant recordedAt) {
    public RegisterMeasurementCommand {
        if (type == null)
            throw new IllegalArgumentException("Measurement type is required");
        if (value == null)
            throw new IllegalArgumentException("Measurement value is required");
    }
}
