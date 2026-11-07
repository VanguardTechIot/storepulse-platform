package com.vanguardtech.storepulse.platform.assets.interfaces.acl;

import java.time.Instant;
import java.util.Optional;

public interface AssetsContextFacade {

    /** Returns the resource (stand or common area) where the device is installed. */
    Optional<Long> fetchResourceIdByIoTDeviceId(Long iotDeviceId);

    /** Tells whether the device exists and is active, so its readings should be processed. */
    boolean isIoTDeviceActive(Long iotDeviceId);

    /** Records that a device reported; called when its telemetry arrives. */
    void recordIoTDeviceReport(Long iotDeviceId, Instant reportedAt, Double supplyVoltage, Integer bufferedEvents);
}