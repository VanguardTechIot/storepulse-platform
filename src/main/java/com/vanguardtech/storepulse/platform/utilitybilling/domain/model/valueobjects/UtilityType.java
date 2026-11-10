package com.vanguardtech.storepulse.platform.utilitybilling.domain.model.valueobjects;

public enum UtilityType {
    WATER("m³"),
    ELECTRICITY("kWh");

    private final String unitOfMeasure;

    UtilityType(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }
}