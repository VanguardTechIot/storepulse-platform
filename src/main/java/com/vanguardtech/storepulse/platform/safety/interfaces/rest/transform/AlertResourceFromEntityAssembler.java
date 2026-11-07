package com.vanguardtech.storepulse.platform.safety.interfaces.rest.transform;

import com.vanguardtech.storepulse.platform.safety.domain.model.aggregates.Alert;
import com.vanguardtech.storepulse.platform.safety.interfaces.rest.resources.AlertResource;

public class AlertResourceFromEntityAssembler {

    public static AlertResource toResourceFromEntity(Alert entity) {
        return new AlertResource(
                entity.getId(),
                entity.getSafetyRuleId(),
                entity.getDeviceId(),
                entity.getMeasurementId(),
                entity.getSensorType(),
                entity.getMeasuredValue(),
                entity.getStatus(),
                entity.getDetectedAt(),
                entity.getAcknowledgedAt(),
                entity.getResolvedAt(),
                entity.getResolutionNote());
    }
}
