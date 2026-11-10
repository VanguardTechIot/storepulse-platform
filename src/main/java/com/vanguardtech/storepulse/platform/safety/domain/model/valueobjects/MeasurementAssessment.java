package com.vanguardtech.storepulse.platform.safety.domain.model.valueobjects;

public record MeasurementAssessment(DeviceStatus deviceStatus, ResponseAction response, boolean alertTriggered) {
}
