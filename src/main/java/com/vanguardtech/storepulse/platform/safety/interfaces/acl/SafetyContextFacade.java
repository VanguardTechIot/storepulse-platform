package com.vanguardtech.storepulse.platform.safety.interfaces.acl;

import java.time.Instant;
import java.util.Map;

public interface SafetyContextFacade {
    SafetyEvaluation evaluate(Long deviceId, Long measurementId, String measurementType, Double value,
                              Instant recordedAt);

    /** @return how many alerts there are in each status (ACTIVE, ACKNOWLEDGED, RESOLVED) */
    Map<String, Long> countAlertsByStatus();

    /** @return how many alerts not resolved yet there are for each sensor type */
    Map<String, Long> countOpenAlertsBySensorType();
}