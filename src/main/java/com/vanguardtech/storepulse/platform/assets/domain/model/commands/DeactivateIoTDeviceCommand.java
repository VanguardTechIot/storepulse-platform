package com.vanguardtech.storepulse.platform.assets.domain.model.commands;

public record DeactivateIoTDeviceCommand(Long iotDeviceId, String reason) {
    public DeactivateIoTDeviceCommand{
        if (iotDeviceId == null || iotDeviceId <= 0) {
            throw new IllegalArgumentException("IoT Device ID must be a positive number.");
        }
    }
}
