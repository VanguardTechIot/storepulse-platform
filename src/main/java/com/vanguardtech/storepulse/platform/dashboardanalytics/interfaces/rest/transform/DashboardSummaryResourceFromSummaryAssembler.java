package com.vanguardtech.storepulse.platform.dashboardanalytics.interfaces.rest.transform;

import com.vanguardtech.storepulse.platform.dashboardanalytics.domain.model.valueobjects.DashboardSummary;
import com.vanguardtech.storepulse.platform.dashboardanalytics.interfaces.rest.resources.DashboardSummaryResource;

public class DashboardSummaryResourceFromSummaryAssembler {

    public static DashboardSummaryResource toResourceFromSummary(DashboardSummary summary) {
        var alerts = summary.alerts();
        var consumption = summary.consumption();
        return new DashboardSummaryResource(
                new DashboardSummaryResource.Alerts(alerts.open(), alerts.active(), alerts.acknowledged(),
                        alerts.resolved(), alerts.openBySensorType()),
                new DashboardSummaryResource.Consumption(consumption.electricity(), consumption.water()));
    }
}