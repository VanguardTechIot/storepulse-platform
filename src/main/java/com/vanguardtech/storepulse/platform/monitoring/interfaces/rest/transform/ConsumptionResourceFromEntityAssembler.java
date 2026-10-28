package com.vanguardtech.storepulse.platform.monitoring.interfaces.rest.transform;

import com.vanguardtech.storepulse.platform.monitoring.domain.model.aggregates.Consumption;
import com.vanguardtech.storepulse.platform.monitoring.interfaces.rest.resources.ConsumptionResource;

public class ConsumptionResourceFromEntityAssembler {

    public static ConsumptionResource toResourceFromEntity(Consumption entity) {
        return new ConsumptionResource(
                entity.getId(),
                entity.getResourceId(),
                entity.getMeasurementType(),
                entity.getTotalValue(),
                entity.getMeasurementType().getUnit(),
                entity.getPeriodStart(),
                entity.getPeriodEnd());
    }
}
