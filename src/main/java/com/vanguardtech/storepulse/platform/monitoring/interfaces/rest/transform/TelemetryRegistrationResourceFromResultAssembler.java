package com.vanguardtech.storepulse.platform.monitoring.interfaces.rest.transform;

import com.vanguardtech.storepulse.platform.monitoring.domain.model.valueobjects.TelemetryRegistration;
import com.vanguardtech.storepulse.platform.monitoring.interfaces.rest.resources.MeasurementEvaluationResource;
import com.vanguardtech.storepulse.platform.monitoring.interfaces.rest.resources.TelemetryRegistrationResource;

public class TelemetryRegistrationResourceFromResultAssembler {

    public static TelemetryRegistrationResource toResourceFromResult(TelemetryRegistration registration) {
        var evaluations = registration.evaluations().stream()
                .map(evaluation -> new MeasurementEvaluationResource(
                        evaluation.type(), evaluation.deviceStatus(), evaluation.response(), evaluation.alertTriggered()))
                .toList();
        return new TelemetryRegistrationResource(
                TelemetryResourceFromEntityAssembler.toResourceFromEntity(registration.telemetry()), evaluations);
    }
}
