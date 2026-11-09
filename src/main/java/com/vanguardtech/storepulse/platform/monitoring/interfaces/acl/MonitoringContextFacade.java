package com.vanguardtech.storepulse.platform.monitoring.interfaces.acl;

import java.time.YearMonth;
import java.util.Collection;

public interface MonitoringContextFacade {

    /** @return the consumption of the resources in the current month, for ELECTRICITY or WATER */
    Double fetchCurrentMonthConsumption(Collection<Long> resourceIds, String measurementType);

    /** @return the consumption of the resource in the month, for ELECTRICITY or WATER, or null without a record */
    Double fetchMonthlyConsumption(Long resourceId, String measurementType, YearMonth month);
}