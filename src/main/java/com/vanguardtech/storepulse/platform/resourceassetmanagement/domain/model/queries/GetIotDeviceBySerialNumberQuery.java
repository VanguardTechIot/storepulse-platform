package com.vanguardtech.storepulse.platform.resourceassetmanagement.domain.model.queries;

public record GetIotDeviceBySerialNumberQuery(String serialNumber) {
    public GetIotDeviceBySerialNumberQuery {
        if (serialNumber == null || serialNumber.isBlank()) {
            throw new IllegalArgumentException("Serial number is required");
        }
    }
}
