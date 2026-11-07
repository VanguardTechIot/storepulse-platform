package com.vanguardtech.storepulse.platform.safety.domain.model.commands;

import com.vanguardtech.storepulse.platform.safety.domain.model.valueobjects.SensorType;

import java.time.Instant;

public record EvaluateMeasurementCommand(Long deviceId, Long measurementId, SensorType sensorType, Double value,
                                         Instant recordedAt) {
    public EvaluateMeasurementCommand {
        if (deviceId == null || deviceId <= 0)
            throw new IllegalArgumentException("Device ID must be a positive number");
        if (measurementId == null || measurementId <= 0)
            throw new IllegalArgumentException("Measurement ID must be a positive number");
        if (sensorType == null)
            throw new IllegalArgumentException("Sensor type is required");
        if (value == null)
            throw new IllegalArgumentException("Measured value is required");
        if (recordedAt == null)
            throw new IllegalArgumentException("Recorded date is required");
    }
}
