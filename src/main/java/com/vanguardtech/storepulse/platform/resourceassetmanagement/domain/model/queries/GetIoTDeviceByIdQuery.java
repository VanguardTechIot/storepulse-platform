package com.vanguardtech.storepulse.platform.resourceassetmanagement.domain.model.queries;

public record GetIoTDeviceByIdQuery(Long iotDeviceId) {
    public GetIoTDeviceByIdQuery{
        if (iotDeviceId == null || iotDeviceId <= 0) {
            throw new IllegalArgumentException("IoT Device ID must be a positive number.");
        }
    }
}
