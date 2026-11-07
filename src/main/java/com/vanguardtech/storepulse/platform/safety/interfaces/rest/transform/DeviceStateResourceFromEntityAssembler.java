package com.vanguardtech.storepulse.platform.safety.interfaces.rest.transform;

import com.vanguardtech.storepulse.platform.safety.domain.model.aggregates.DeviceState;
import com.vanguardtech.storepulse.platform.safety.interfaces.rest.resources.DeviceStateResource;

public class DeviceStateResourceFromEntityAssembler {

    public static DeviceStateResource toResourceFromEntity(DeviceState entity) {
        return new DeviceStateResource(
                entity.getDeviceId(), entity.getStatus(), entity.getResponse(), entity.getEvaluatedAt());
    }
}
