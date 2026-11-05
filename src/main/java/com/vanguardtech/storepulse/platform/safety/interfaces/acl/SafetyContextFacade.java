package com.vanguardtech.storepulse.platform.safety.interfaces.acl;

import java.time.Instant;

public interface SafetyContextFacade {
    SafetyEvaluation evaluate(Long deviceId, Long measurementId, String measurementType, Double value,
                              Instant recordedAt);
}
