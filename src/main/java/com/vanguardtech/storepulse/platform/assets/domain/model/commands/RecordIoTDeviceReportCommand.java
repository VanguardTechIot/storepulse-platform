package com.vanguardtech.storepulse.platform.assets.domain.model.commands;


import java.time.Instant;

/**
 * Command to record that an IoT device reported to the platform.
 *
 * @param iotDeviceId    id of the reporting device
 * @param reportedAt     time of the report
 * @param supplyVoltage  supply voltage in volts, or null if the report does not include it
 * @param bufferedEvents events kept on the device while offline, or null if not included
 */
public record RecordIoTDeviceReportCommand(Long iotDeviceId, Instant reportedAt, Double supplyVoltage,
                                           Integer bufferedEvents) {
    public RecordIoTDeviceReportCommand {
        if (iotDeviceId == null || iotDeviceId <= 0)
            throw new IllegalArgumentException("IoT device id must be a positive number");
        if (reportedAt == null)
            throw new IllegalArgumentException("Report time is required");
        if (bufferedEvents != null && bufferedEvents < 0)
            throw new IllegalArgumentException("Buffered events cannot be negative");
    }
}
