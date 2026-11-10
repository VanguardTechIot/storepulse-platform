package com.vanguardtech.storepulse.platform.monitoring.domain.model.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record MeasurementValue(@Column(name = "measured_value", nullable = false) Double value) {

    public MeasurementValue {
        if (value == null || value.isNaN() || value.isInfinite())
            throw new IllegalArgumentException("Measurement value must be a valid number");
    }

    public boolean isPositive() {
        return value > 0;
    }
}
