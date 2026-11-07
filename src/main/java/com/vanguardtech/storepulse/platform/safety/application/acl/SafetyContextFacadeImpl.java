package com.vanguardtech.storepulse.platform.safety.application.acl;

import com.vanguardtech.storepulse.platform.safety.domain.model.commands.EvaluateMeasurementCommand;
import com.vanguardtech.storepulse.platform.safety.domain.model.queries.GetAllAlertsQuery;
import com.vanguardtech.storepulse.platform.safety.domain.model.valueobjects.AlertStatus;
import com.vanguardtech.storepulse.platform.safety.domain.model.valueobjects.SensorType;
import com.vanguardtech.storepulse.platform.safety.domain.services.AlertCommandService;
import com.vanguardtech.storepulse.platform.safety.domain.services.AlertQueryService;
import com.vanguardtech.storepulse.platform.safety.interfaces.acl.SafetyContextFacade;
import com.vanguardtech.storepulse.platform.safety.interfaces.acl.SafetyEvaluation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SafetyContextFacadeImpl implements SafetyContextFacade {

    private final AlertCommandService alertCommandService;
    private final AlertQueryService alertQueryService;

    @Override
    public SafetyEvaluation evaluate(Long deviceId, Long measurementId, String measurementType, Double value,
                                     Instant recordedAt) {
        var assessment = alertCommandService.handle(new EvaluateMeasurementCommand(
                deviceId, measurementId, SensorType.valueOf(measurementType), value, recordedAt));
        return new SafetyEvaluation(
                assessment.deviceStatus().name(), assessment.response().name(), assessment.alertTriggered());
    }

    @Override
    public Map<String, Long> countAlertsByStatus() {
        return alertQueryService.handle(new GetAllAlertsQuery()).stream()
                .collect(Collectors.groupingBy(alert -> alert.getStatus().name(), Collectors.counting()));
    }

    @Override
    public Map<String, Long> countOpenAlertsBySensorType() {
        return alertQueryService.handle(new GetAllAlertsQuery()).stream()
                .filter(alert -> alert.getStatus() != AlertStatus.RESOLVED)
                .collect(Collectors.groupingBy(alert -> alert.getSensorType().name(), Collectors.counting()));
    }
}