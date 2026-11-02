package com.vanguardtech.storepulse.platform.safety.domain.model.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
public record Threshold(
        @Column(name = "threshold_value", nullable = false) Double value,
        @Enumerated(EnumType.STRING) @Column(name = "threshold_operator", nullable = false) ComparisonOperator operator) {

    public Threshold {
        if (value == null || value.isNaN() || value.isInfinite())
            throw new IllegalArgumentException("Threshold value must be a valid number");
        if (operator == null)
            throw new IllegalArgumentException("Threshold operator is required");
    }

    public boolean isMetBy(Double measuredValue) {
        return operator.compare(measuredValue, value);
    }
}
