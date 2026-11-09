package com.vanguardtech.storepulse.platform.safety.domain.model.queries;

import java.util.Collection;

public record GetAlertsByDeviceIdsQuery(Collection<Long> deviceIds) {
    public GetAlertsByDeviceIdsQuery {
        if (deviceIds == null)
            throw new IllegalArgumentException("Device IDs are required");
    }
}