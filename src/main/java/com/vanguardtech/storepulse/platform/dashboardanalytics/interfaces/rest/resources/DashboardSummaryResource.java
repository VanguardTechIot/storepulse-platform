package com.vanguardtech.storepulse.platform.dashboardanalytics.interfaces.rest.resources;

import java.util.Map;

public record DashboardSummaryResource(Alerts alerts, Consumption consumption) {

    public record Alerts(long open, long active, long acknowledged, long resolved, Map<String, Long> openBySensorType) {
    }

    public record Consumption(double electricityKwh, double waterCubicMeters) {
    }
}