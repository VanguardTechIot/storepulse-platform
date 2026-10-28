package com.vanguardtech.storepulse.platform.monitoring.domain.model.queries;

public record GetSensorStatusByDeviceIdQuery(Long deviceId) {
    public GetSensorStatusByDeviceIdQuery {
        if (deviceId == null || deviceId <= 0)
            throw new IllegalArgumentException("Device ID must be a positive number");
    }
}
