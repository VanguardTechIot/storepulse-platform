package com.vanguardtech.storepulse.platform.assets.domain.model.commands;

public record ReactivateIoTDeviceCommand(Long iotDeviceId) {
    public ReactivateIoTDeviceCommand {
        if (iotDeviceId == null || iotDeviceId <= 0) {
            throw new IllegalArgumentException("iotDeviceId must be a positive number");
        }
    }
}
