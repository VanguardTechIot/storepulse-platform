package com.vanguardtech.storepulse.platform.monitoring.interfaces.acl;

import java.time.YearMonth;
import java.util.Collection;

public interface MonitoringContextFacade {

    Double fetchCurrentMonthConsumption(Collection<Long> resourceIds, String measurementType);

    Double fetchMonthlyConsumption(Long resourceId, String measurementType, YearMonth month);
}