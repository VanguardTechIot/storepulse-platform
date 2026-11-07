package com.vanguardtech.storepulse.platform.monitoring.interfaces.acl;

import java.time.YearMonth;

public interface MonitoringContextFacade {
    Double fetchLatestMeasurementValue(Long deviceId, String measurementType);

    Double fetchTotalConsumption(Long resourceId, String measurementType);

    /** @return the consumption of every resource in the current month, for ELECTRICITY or WATER */
    Double fetchCurrentMonthConsumption(String measurementType);

    /** @return the consumption of the resource in the month, for ELECTRICITY or WATER, or null without a record */
    Double fetchMonthlyConsumption(Long resourceId, String measurementType, YearMonth month);
}