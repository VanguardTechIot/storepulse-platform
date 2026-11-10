package com.vanguardtech.storepulse.platform.monitoring.domain.model.commands;

import java.time.Instant;
import java.util.List;

public record RegisterTelemetryCommand(Long deviceId, Instant receivedAt, Double supplyVoltage, Integer bufferedEvents,
                                       List<RegisterMeasurementCommand> measurements) {
    public RegisterTelemetryCommand {
        if (deviceId == null || deviceId <= 0)
            throw new IllegalArgumentException("Device ID must be a positive number");
        if (supplyVoltage != null && (supplyVoltage.isNaN() || supplyVoltage < 0))
            throw new IllegalArgumentException("Supply voltage must not be negative");
        if (bufferedEvents != null && bufferedEvents < 0)
            throw new IllegalArgumentException("Buffered events must not be negative");
        if (measurements == null || measurements.isEmpty())
            throw new IllegalArgumentException("Telemetry must include at least one measurement");
        measurements = List.copyOf(measurements);
    }
}
