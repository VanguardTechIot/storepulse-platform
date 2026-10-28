package com.vanguardtech.storepulse.platform.assets.domain.model.commands;

import com.vanguardtech.storepulse.platform.assets.domain.model.valueobjects.IoTDeviceType;

public record RegisterIoTDeviceCommand(String serialNumber, IoTDeviceType type, Long resourceId) {
    public RegisterIoTDeviceCommand{
        if (serialNumber == null || serialNumber.isBlank())
            throw new IllegalArgumentException("Serial number is required");
        if (type == null)
            throw new IllegalArgumentException("Device type is required");
        if (resourceId == null || resourceId <= 0)
            throw new IllegalArgumentException("Resource ID must be a positive number");
    }
}
