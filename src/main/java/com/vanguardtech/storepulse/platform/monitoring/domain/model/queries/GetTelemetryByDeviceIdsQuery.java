package com.vanguardtech.storepulse.platform.monitoring.domain.model.queries;

import java.util.Collection;

public record GetTelemetryByDeviceIdsQuery(Collection<Long> deviceIds) {
    public GetTelemetryByDeviceIdsQuery {
        if (deviceIds == null)
            throw new IllegalArgumentException("Device IDs are required");
    }
}