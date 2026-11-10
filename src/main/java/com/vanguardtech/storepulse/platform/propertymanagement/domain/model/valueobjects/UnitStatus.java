package com.vanguardtech.storepulse.platform.propertymanagement.domain.model.valueobjects;

/** Occupancy of a unit. Common areas are always AVAILABLE or under MAINTENANCE. */
public enum UnitStatus {
    AVAILABLE,
    OCCUPIED,
    MAINTENANCE
}
