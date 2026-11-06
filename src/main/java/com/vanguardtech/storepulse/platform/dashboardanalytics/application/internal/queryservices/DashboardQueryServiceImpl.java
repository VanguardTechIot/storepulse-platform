package com.vanguardtech.storepulse.platform.dashboardanalytics.application.internal.queryservices;

import com.vanguardtech.storepulse.platform.dashboardanalytics.application.internal.outboundservices.acl.ExternalMonitoringService;
import com.vanguardtech.storepulse.platform.dashboardanalytics.application.internal.outboundservices.acl.ExternalSafetyService;
import com.vanguardtech.storepulse.platform.dashboardanalytics.domain.model.queries.GetDashboardSummaryQuery;
import com.vanguardtech.storepulse.platform.dashboardanalytics.domain.model.valueobjects.DashboardSummary;
import com.vanguardtech.storepulse.platform.dashboardanalytics.domain.services.DashboardQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardQueryServiceImpl implements DashboardQueryService {

    private final ExternalSafetyService externalSafetyService;
    private final ExternalMonitoringService externalMonitoringService;

    @Override
    public DashboardSummary handle(GetDashboardSummaryQuery query) {
        return new DashboardSummary(externalSafetyService.fetchAlertsSummary(),
                externalMonitoringService.fetchCurrentMonthConsumption());
    }
}