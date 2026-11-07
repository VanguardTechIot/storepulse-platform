package com.vanguardtech.storepulse.platform.safety.interfaces.rest.resources;

import com.vanguardtech.storepulse.platform.safety.domain.model.valueobjects.ComparisonOperator;
import com.vanguardtech.storepulse.platform.safety.domain.model.valueobjects.DeviceStatus;
import com.vanguardtech.storepulse.platform.safety.domain.model.valueobjects.ResponseAction;
import com.vanguardtech.storepulse.platform.safety.domain.model.valueobjects.SensorType;

public record SafetyRuleResource(
        Long id,
        String name,
        SensorType sensorType,
        Double thresholdValue,
        ComparisonOperator thresholdOperator,
        DeviceStatus deviceStatus,
        ResponseAction responseAction,
        boolean enabled) {
}
