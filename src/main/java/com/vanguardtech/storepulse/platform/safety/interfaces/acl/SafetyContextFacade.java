package com.vanguardtech.storepulse.platform.safety.interfaces.acl;

import java.time.Instant;
import java.util.Collection;
import java.util.Map;

public interface SafetyContextFacade {
    SafetyEvaluation evaluate(Long deviceId, Long measurementId, String measurementType, Double value,
                              Instant recordedAt);

    /** @return how many alerts of the devices there are in each status (ACTIVE, ACKNOWLEDGED, RESOLVED) */
    Map<String, Long> countAlertsByStatus(Collection<Long> deviceIds);

    /** @return how many alerts of the devices not resolved yet there are for each sensor type */
    Map<String, Long> countOpenAlertsBySensorType(Collection<Long> deviceIds);
}