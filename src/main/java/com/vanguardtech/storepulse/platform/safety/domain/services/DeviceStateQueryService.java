package com.vanguardtech.storepulse.platform.safety.domain.services;

import com.vanguardtech.storepulse.platform.safety.domain.model.aggregates.DeviceState;
import com.vanguardtech.storepulse.platform.safety.domain.model.queries.GetDeviceStateByDeviceIdQuery;

import java.util.Optional;

public interface DeviceStateQueryService {
    Optional<DeviceState> handle(GetDeviceStateByDeviceIdQuery query);
}
