package com.vanguardtech.storepulse.platform.monitoring.interfaces.rest.transform;

import com.vanguardtech.storepulse.platform.monitoring.domain.model.entities.Measurement;
import com.vanguardtech.storepulse.platform.monitoring.interfaces.rest.resources.MeasurementResource;

public class MeasurementResourceFromEntityAssembler {

    public static MeasurementResource toResourceFromEntity(Measurement entity) {
        return new MeasurementResource(
                entity.getId(),
                entity.getType(),
                entity.getValue().value(),
                entity.getType().getUnit(),
                entity.getRecordedAt());
    }
}
