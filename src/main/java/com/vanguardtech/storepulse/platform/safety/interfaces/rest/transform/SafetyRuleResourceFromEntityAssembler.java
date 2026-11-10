package com.vanguardtech.storepulse.platform.safety.interfaces.rest.transform;

import com.vanguardtech.storepulse.platform.safety.domain.model.aggregates.SafetyRule;
import com.vanguardtech.storepulse.platform.safety.interfaces.rest.resources.SafetyRuleResource;

public class SafetyRuleResourceFromEntityAssembler {

    public static SafetyRuleResource toResourceFromEntity(SafetyRule entity) {
        return new SafetyRuleResource(
                entity.getId(),
                entity.getName(),
                entity.getSensorType(),
                entity.getThreshold().value(),
                entity.getThreshold().operator(),
                entity.getDeviceStatus(),
                entity.getResponseAction(),
                entity.isEnabled());
    }
}
