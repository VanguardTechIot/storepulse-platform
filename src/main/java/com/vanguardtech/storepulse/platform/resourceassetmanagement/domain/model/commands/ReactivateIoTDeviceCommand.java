package com.vanguardtech.storepulse.platform.resourceassetmanagement.domain.model.commands;

public record ReactivateIoTDeviceCommand(Long iotDeviceId) {
    public ReactivateIoTDeviceCommand {
        if (iotDeviceId == null || iotDeviceId <= 0) {
            throw new IllegalArgumentException("iotDeviceId must be a positive number");
        }
    }
}
