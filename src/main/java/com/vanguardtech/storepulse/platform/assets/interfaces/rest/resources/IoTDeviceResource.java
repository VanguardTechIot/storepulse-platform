package com.vanguardtech.storepulse.platform.assets.interfaces.rest.resources;

import java.time.Instant;
import java.util.List;

public record IoTDeviceResource(
        Long id,
        Long assetId,
        Long resourceId,
        String serialNumber,
        String manufacturer,
        String firmwareVersion,
        String type,
        String status,
        String connectivity,
        Instant lastReportAt,
        Double supplyVoltage,
        int bufferedEvents,
        String deactivationReason,
        List<String> actuators) {
}
