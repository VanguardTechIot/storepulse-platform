package com.vanguardtech.storepulse.platform.resourceassetmanagement.domain.model.queries;

public record GetIoTDeviceBySerialNumberQuery(String serialNumber) {
    public GetIoTDeviceBySerialNumberQuery {
        if (serialNumber == null || serialNumber.isBlank()) {
            throw new IllegalArgumentException("Serial number is required");
        }
    }
}
