package com.vanguardtech.storepulse.platform.monitoring.interfaces.acl;

public interface MonitoringContextFacade {
    Double fetchLatestMeasurementValue(Long deviceId, String measurementType);

    Double fetchTotalConsumption(Long resourceId, String measurementType);
}
