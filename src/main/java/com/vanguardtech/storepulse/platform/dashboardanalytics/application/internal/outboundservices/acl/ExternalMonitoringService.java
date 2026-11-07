package com.vanguardtech.storepulse.platform.dashboardanalytics.application.internal.outboundservices.acl;

import com.vanguardtech.storepulse.platform.dashboardanalytics.domain.model.valueobjects.ConsumptionSummary;
import com.vanguardtech.storepulse.platform.monitoring.interfaces.acl.MonitoringContextFacade;
import org.springframework.stereotype.Service;

/** Reads from Monitoring the consumption of the current month. */
@Service("analyticsExternalMonitoringService")
public class ExternalMonitoringService {

    private final MonitoringContextFacade monitoringContextFacade;

    public ExternalMonitoringService(MonitoringContextFacade monitoringContextFacade) {
        this.monitoringContextFacade = monitoringContextFacade;
    }

    public ConsumptionSummary fetchCurrentMonthConsumption() {
        return new ConsumptionSummary(monitoringContextFacade.fetchCurrentMonthConsumption("ELECTRICITY"),
                monitoringContextFacade.fetchCurrentMonthConsumption("WATER"));
    }
}