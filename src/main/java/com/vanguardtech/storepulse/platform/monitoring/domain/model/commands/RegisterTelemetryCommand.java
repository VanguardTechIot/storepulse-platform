package com.vanguardtech.storepulse.platform.monitoring.domain.model.commands;

import java.time.Instant;
import java.util.List;

public record RegisterTelemetryCommand(Long deviceId, Instant receivedAt, List<RegisterMeasurementCommand> measurements) {
    public RegisterTelemetryCommand {
        if (deviceId == null || deviceId <= 0)
            throw new IllegalArgumentException("Device ID must be a positive number");
        if (measurements == null || measurements.isEmpty())
            throw new IllegalArgumentException("Telemetry must include at least one measurement");
        measurements = List.copyOf(measurements);
    }
}
