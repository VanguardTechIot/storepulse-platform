package com.vanguardtech.storepulse.platform.safety.interfaces.rest.resources;

import com.vanguardtech.storepulse.platform.safety.domain.model.valueobjects.DeviceStatus;
import com.vanguardtech.storepulse.platform.safety.domain.model.valueobjects.ResponseAction;

import java.time.Instant;

public record DeviceStateResource(Long deviceId, DeviceStatus status, ResponseAction response, Instant evaluatedAt) {
}
