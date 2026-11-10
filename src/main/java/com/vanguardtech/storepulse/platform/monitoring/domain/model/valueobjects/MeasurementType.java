package com.vanguardtech.storepulse.platform.monitoring.domain.model.valueobjects;

public enum MeasurementType {
    TEMPERATURE("°C"),
    HUMIDITY("%"),
    ELECTRICITY("kWh"),
    WATER("m³"),
    MOTION(""),
    SMOKE("ppm");

    private final String unit;

    MeasurementType(String unit) {
        this.unit = unit;
    }

    public String getUnit() {
        return unit;
    }

    public boolean isConsumption() {
        return this == ELECTRICITY || this == WATER;
    }
}
