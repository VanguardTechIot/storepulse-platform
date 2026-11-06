package com.vanguardtech.storepulse.platform.dashboardanalytics.domain.services;

import com.vanguardtech.storepulse.platform.dashboardanalytics.domain.model.queries.GetDashboardSummaryQuery;
import com.vanguardtech.storepulse.platform.dashboardanalytics.domain.model.valueobjects.DashboardSummary;

public interface DashboardQueryService {
    DashboardSummary handle(GetDashboardSummaryQuery query);
}