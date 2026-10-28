package com.vanguardtech.storepulse.platform.monitoring.interfaces.rest.transform;

import com.vanguardtech.storepulse.platform.monitoring.domain.model.aggregates.Telemetry;
import com.vanguardtech.storepulse.platform.monitoring.interfaces.rest.resources.TelemetryResource;

public class TelemetryResourceFromEntityAssembler {

    public static TelemetryResource toResourceFromEntity(Telemetry entity) {
        var measurements = entity.getMeasurements().stream()
                .map(MeasurementResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return new TelemetryResource(entity.getId(), entity.getDeviceId(), entity.getReceivedAt(), measurements);
    }
}
