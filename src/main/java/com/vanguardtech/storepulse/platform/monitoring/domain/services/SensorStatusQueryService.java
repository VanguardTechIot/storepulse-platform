package com.vanguardtech.storepulse.platform.monitoring.domain.services;

import com.vanguardtech.storepulse.platform.monitoring.domain.model.queries.GetSensorStatusByDeviceIdQuery;
import com.vanguardtech.storepulse.platform.monitoring.domain.model.queries.GetSensorStatusByDeviceIdsQuery;
import com.vanguardtech.storepulse.platform.monitoring.domain.model.valueobjects.SensorStatus;

import java.util.List;

public interface SensorStatusQueryService {
    List<SensorStatus> handle(GetSensorStatusByDeviceIdQuery query);

    List<SensorStatus> handle(GetSensorStatusByDeviceIdsQuery query);
}
