package com.vanguardtech.storepulse.platform.monitoring.domain.model.valueobjects;

import com.vanguardtech.storepulse.platform.monitoring.domain.model.aggregates.Telemetry;

import java.util.List;

public record TelemetryRegistration(Telemetry telemetry, List<MeasurementEvaluation> evaluations) {
}
