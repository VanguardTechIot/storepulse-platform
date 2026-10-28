package com.vanguardtech.storepulse.platform.monitoring.interfaces.rest.resources;

import com.vanguardtech.storepulse.platform.monitoring.domain.model.valueobjects.MeasurementType;

import java.time.Instant;

public record ConsumptionResource(
        Long id,
        Long resourceId,
        MeasurementType measurementType,
        Double totalValue,
        String unit,
        Instant periodStart,
        Instant periodEnd) {
}
