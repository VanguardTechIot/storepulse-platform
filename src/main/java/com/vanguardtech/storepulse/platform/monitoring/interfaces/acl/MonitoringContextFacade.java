package com.vanguardtech.storepulse.platform.monitoring.interfaces.acl;

public interface MonitoringContextFacade {
    Double fetchLatestMeasurementValue(Long deviceId, String measurementType);

    Double fetchTotalConsumption(Long resourceId, String measurementType);

    /** @return the consumption of every resource in the current month, for ELECTRICITY or WATER */
    Double fetchCurrentMonthConsumption(String measurementType);
}