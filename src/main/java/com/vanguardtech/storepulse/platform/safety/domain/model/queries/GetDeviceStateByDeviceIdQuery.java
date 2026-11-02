package com.vanguardtech.storepulse.platform.safety.domain.model.queries;

public record GetDeviceStateByDeviceIdQuery(Long deviceId) {
    public GetDeviceStateByDeviceIdQuery {
        if (deviceId == null || deviceId <= 0)
            throw new IllegalArgumentException("Device ID must be a positive number");
    }
}
