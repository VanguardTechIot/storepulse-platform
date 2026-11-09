package com.vanguardtech.storepulse.platform.dashboardanalytics.application.internal.outboundservices.acl;

import com.vanguardtech.storepulse.platform.dashboardanalytics.domain.model.valueobjects.AlertsSummary;
import com.vanguardtech.storepulse.platform.safety.interfaces.acl.SafetyContextFacade;
import org.springframework.stereotype.Service;

import java.util.Collection;

/** Reads from Safety how many alerts there are, without touching its model. */
@Service("analyticsExternalSafetyService")
public class ExternalSafetyService {

    private final SafetyContextFacade safetyContextFacade;

    public ExternalSafetyService(SafetyContextFacade safetyContextFacade) {
        this.safetyContextFacade = safetyContextFacade;
    }

    public AlertsSummary fetchAlertsSummary(Collection<Long> deviceIds) {
        var byStatus = safetyContextFacade.countAlertsByStatus(deviceIds);
        return new AlertsSummary(byStatus.getOrDefault("ACTIVE", 0L), byStatus.getOrDefault("ACKNOWLEDGED", 0L),
                byStatus.getOrDefault("RESOLVED", 0L), safetyContextFacade.countOpenAlertsBySensorType(deviceIds));
    }
}