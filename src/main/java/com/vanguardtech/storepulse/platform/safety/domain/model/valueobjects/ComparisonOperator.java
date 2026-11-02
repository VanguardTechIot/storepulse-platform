package com.vanguardtech.storepulse.platform.safety.domain.model.valueobjects;

public enum ComparisonOperator {
    GREATER_THAN,
    GREATER_THAN_OR_EQUAL,
    LESS_THAN,
    LESS_THAN_OR_EQUAL,
    EQUAL;

    public boolean compare(double measuredValue, double limit) {
        return switch (this) {
            case GREATER_THAN -> measuredValue > limit;
            case GREATER_THAN_OR_EQUAL -> measuredValue >= limit;
            case LESS_THAN -> measuredValue < limit;
            case LESS_THAN_OR_EQUAL -> measuredValue <= limit;
            case EQUAL -> Double.compare(measuredValue, limit) == 0;
        };
    }
}
