package com.vanguardtech.storepulse.platform.monitoring.domain.model.valueobjects;

public record MeasurementEvaluation(MeasurementType type, String deviceStatus, String response, boolean alertTriggered) {
}
