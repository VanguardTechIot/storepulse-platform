package com.vanguardtech.storepulse.platform.monitoring.interfaces.rest.resources;

import java.util.List;

public record TelemetryRegistrationResource(TelemetryResource telemetry, List<MeasurementEvaluationResource> evaluations) {
}
