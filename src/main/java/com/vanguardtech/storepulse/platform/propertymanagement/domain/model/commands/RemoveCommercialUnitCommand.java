package com.vanguardtech.storepulse.platform.propertymanagement.domain.model.commands;

/** Logical removal of a unit: the row is kept for the history of bills and devices. */
public record RemoveCommercialUnitCommand(Long unitId, Long administratorId) {
}