package com.vanguardtech.storepulse.platform.monitoring.domain.model.queries;

public record GetTelemetryByDeviceIdQuery(Long deviceId) {
    public GetTelemetryByDeviceIdQuery {
        if (deviceId == null || deviceId <= 0)
            throw new IllegalArgumentException("Device ID must be a positive number");
    }
}
