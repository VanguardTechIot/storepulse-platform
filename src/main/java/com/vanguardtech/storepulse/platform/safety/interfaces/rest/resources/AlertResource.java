package com.vanguardtech.storepulse.platform.safety.interfaces.rest.resources;

import com.vanguardtech.storepulse.platform.safety.domain.model.valueobjects.AlertStatus;
import com.vanguardtech.storepulse.platform.safety.domain.model.valueobjects.SensorType;

import java.time.Instant;

public record AlertResource(
        Long id,
        Long safetyRuleId,
        Long deviceId,
        Long measurementId,
        SensorType sensorType,
        Double measuredValue,
        AlertStatus status,
        Instant detectedAt,
        Instant acknowledgedAt,
        Instant resolvedAt,
        String resolutionNote) {
}
