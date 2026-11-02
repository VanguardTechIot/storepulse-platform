package com.vanguardtech.storepulse.platform.safety.application.acl;

import com.vanguardtech.storepulse.platform.safety.domain.model.commands.EvaluateMeasurementCommand;
import com.vanguardtech.storepulse.platform.safety.domain.model.valueobjects.SensorType;
import com.vanguardtech.storepulse.platform.safety.domain.services.AlertCommandService;
import com.vanguardtech.storepulse.platform.safety.interfaces.acl.SafetyContextFacade;
import com.vanguardtech.storepulse.platform.safety.interfaces.acl.SafetyEvaluation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class SafetyContextFacadeImpl implements SafetyContextFacade {

    private final AlertCommandService alertCommandService;

    @Override
    public SafetyEvaluation evaluate(Long deviceId, String measurementType, Double value, Instant recordedAt) {
        var assessment = alertCommandService.handle(new EvaluateMeasurementCommand(
                deviceId, SensorType.valueOf(measurementType), value, recordedAt));
        return new SafetyEvaluation(
                assessment.deviceStatus().name(), assessment.response().name(), assessment.alertTriggered());
    }
}
