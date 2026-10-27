package com.vanguardtech.storepulse.platform.resourceassetmanagement.domain.model.commands;

import com.vanguardtech.storepulse.platform.resourceassetmanagement.domain.model.valueobjects.IoTDeviceType;

public record RegisterIoTDeviceCommand(String serialNumber, IoTDeviceType Type, Long resourceId) {
    public RegisterIoTDeviceCommand{
        if (serialNumber == null || serialNumber.isBlank())
            throw new IllegalArgumentException("Serial number is required");
        if (Type == null)
            throw new IllegalArgumentException("Device type is required");
        if (resourceId == null || resourceId <= 0)
            throw new IllegalArgumentException("Resource ID must be a positive number");
    }
}
