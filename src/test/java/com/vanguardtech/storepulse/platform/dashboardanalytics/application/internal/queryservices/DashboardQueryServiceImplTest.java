package com.vanguardtech.storepulse.platform.dashboardanalytics.application.internal.queryservices;

import com.vanguardtech.storepulse.platform.dashboardanalytics.application.internal.outboundservices.acl.ExternalAssetsService;
import com.vanguardtech.storepulse.platform.dashboardanalytics.application.internal.outboundservices.acl.ExternalMonitoringService;
import com.vanguardtech.storepulse.platform.dashboardanalytics.application.internal.outboundservices.acl.ExternalPropertyCommunicationService;
import com.vanguardtech.storepulse.platform.dashboardanalytics.application.internal.outboundservices.acl.ExternalSafetyService;
import com.vanguardtech.storepulse.platform.dashboardanalytics.domain.model.queries.GetDashboardSummaryQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardQueryServiceImplTest {

    @Mock
    private ExternalPropertyCommunicationService externalPropertyCommunicationService;
    @Mock
    private ExternalAssetsService externalAssetsService;
    @Mock
    private ExternalSafetyService externalSafetyService;
    @Mock
    private ExternalMonitoringService externalMonitoringService;

    @InjectMocks
    private DashboardQueryServiceImpl service;

    @Test
    void summarizesOnlyTheResourcesAndDevicesVisibleToTheUser() {
        when(externalPropertyCommunicationService.fetchResourceIdsVisibleToAuthenticatedUser()).thenReturn(List.of(4L));
        when(externalAssetsService.fetchDeviceIdsByResourceIds(List.of(4L))).thenReturn(List.of(8L));

        service.handle(new GetDashboardSummaryQuery());

        verify(externalSafetyService).fetchAlertsSummary(List.of(8L));
        verify(externalMonitoringService).fetchCurrentMonthConsumption(List.of(4L));
    }
}