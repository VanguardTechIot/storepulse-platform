package com.vanguardtech.storepulse.platform.safety.interfaces.acl;

import java.time.Instant;
import java.util.Collection;
import java.util.Map;

public interface SafetyContextFacade {
    SafetyEvaluation evaluate(Long deviceId, Long measurementId, String measurementType, Double value,
                              Instant recordedAt);

    Map<String, Long> countAlertsByStatus(Collection<Long> deviceIds);

    Map<String, Long> countOpenAlertsBySensorType(Collection<Long> deviceIds);
}