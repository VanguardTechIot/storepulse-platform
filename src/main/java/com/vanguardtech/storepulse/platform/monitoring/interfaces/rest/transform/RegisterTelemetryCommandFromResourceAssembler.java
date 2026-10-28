package com.vanguardtech.storepulse.platform.monitoring.interfaces.rest.transform;

import com.vanguardtech.storepulse.platform.monitoring.domain.model.commands.RegisterMeasurementCommand;
import com.vanguardtech.storepulse.platform.monitoring.domain.model.commands.RegisterTelemetryCommand;
import com.vanguardtech.storepulse.platform.monitoring.interfaces.rest.resources.RegisterTelemetryResource;

public class RegisterTelemetryCommandFromResourceAssembler {

    public static RegisterTelemetryCommand toCommandFromResource(RegisterTelemetryResource resource) {
        var measurements = resource.measurements().stream()
                .map(measurement -> new RegisterMeasurementCommand(
                        measurement.type(), measurement.value(), measurement.recordedAt()))
                .toList();
        return new RegisterTelemetryCommand(resource.deviceId(), resource.receivedAt(), measurements);
    }
}
