package com.vanguardtech.storepulse.platform.monitoring.domain.model.queries;

import java.util.Collection;

public record GetSensorStatusByDeviceIdsQuery(Collection<Long> deviceIds) {
    public GetSensorStatusByDeviceIdsQuery {
        if (deviceIds == null)
            throw new IllegalArgumentException("Device IDs are required");
    }
}
