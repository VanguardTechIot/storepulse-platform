package com.vanguardtech.storepulse.platform.propertymanagement.domain.model.commands;

/**
 * Marks a store as occupied or available. Used by Property Communication when a tenant assignment
 * starts or ends.
 */
public record ChangeUnitOccupancyCommand(Long unitId, boolean occupied) {
}
