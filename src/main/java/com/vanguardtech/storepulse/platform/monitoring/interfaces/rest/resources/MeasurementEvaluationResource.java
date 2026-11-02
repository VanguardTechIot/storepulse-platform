package com.vanguardtech.storepulse.platform.monitoring.interfaces.rest.resources;

import com.vanguardtech.storepulse.platform.monitoring.domain.model.valueobjects.MeasurementType;

public record MeasurementEvaluationResource(
        MeasurementType type,
        String deviceStatus,
        String response,
        boolean alertTriggered) {
}
