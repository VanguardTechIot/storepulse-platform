package com.vanguardtech.storepulse.platform.dashboardanalytics.application.internal.queryservices;

import com.vanguardtech.storepulse.platform.dashboardanalytics.application.internal.outboundservices.acl.ExternalAssetsService;
import com.vanguardtech.storepulse.platform.dashboardanalytics.application.internal.outboundservices.acl.ExternalMonitoringService;
import com.vanguardtech.storepulse.platform.dashboardanalytics.application.internal.outboundservices.acl.ExternalPropertyCommunicationService;
import com.vanguardtech.storepulse.platform.dashboardanalytics.application.internal.outboundservices.acl.ExternalSafetyService;
import com.vanguardtech.storepulse.platform.dashboardanalytics.domain.model.queries.GetDashboardSummaryQuery;
import com.vanguardtech.storepulse.platform.dashboardanalytics.domain.model.valueobjects.DashboardSummary;
import com.vanguardtech.storepulse.platform.dashboardanalytics.domain.services.DashboardQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** Builds the summary only with the resources and devices visible to the authenticated user. */
@Service
@RequiredArgsConstructor
public class DashboardQueryServiceImpl implements DashboardQueryService {

    private final ExternalPropertyCommunicationService externalPropertyCommunicationService;
    private final ExternalAssetsService externalAssetsService;
    private final ExternalSafetyService externalSafetyService;
    private final ExternalMonitoringService externalMonitoringService;

    @Override
    public DashboardSummary handle(GetDashboardSummaryQuery query) {
        var resourceIds = externalPropertyCommunicationService.fetchResourceIdsVisibleToAuthenticatedUser();
        var deviceIds = externalAssetsService.fetchDeviceIdsByResourceIds(resourceIds);
        return new DashboardSummary(externalSafetyService.fetchAlertsSummary(deviceIds),
                externalMonitoringService.fetchCurrentMonthConsumption(resourceIds));
    }
}