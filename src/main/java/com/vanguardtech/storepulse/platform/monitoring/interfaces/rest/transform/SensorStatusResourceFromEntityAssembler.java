package com.vanguardtech.storepulse.platform.monitoring.interfaces.rest.transform;

import com.vanguardtech.storepulse.platform.monitoring.domain.model.valueobjects.SensorStatus;
import com.vanguardtech.storepulse.platform.monitoring.interfaces.rest.resources.SensorStatusResource;

public class SensorStatusResourceFromEntityAssembler {

    public static SensorStatusResource toResourceFromEntity(SensorStatus status) {
        return new SensorStatusResource(status.type(), status.value(), status.type().getUnit(), status.recordedAt());
    }
}
