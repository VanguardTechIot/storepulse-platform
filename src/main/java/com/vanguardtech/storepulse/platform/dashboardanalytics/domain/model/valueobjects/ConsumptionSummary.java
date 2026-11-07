package com.vanguardtech.storepulse.platform.dashboardanalytics.domain.model.valueobjects;

/** Electricity (kWh) and water (m³) consumed in the current month. */
public record ConsumptionSummary(double electricity, double water) {
}