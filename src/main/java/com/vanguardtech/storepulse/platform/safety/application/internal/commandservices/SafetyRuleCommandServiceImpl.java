package com.vanguardtech.storepulse.platform.safety.application.internal.commandservices;

import com.vanguardtech.storepulse.platform.safety.domain.model.aggregates.SafetyRule;
import com.vanguardtech.storepulse.platform.safety.domain.model.commands.SeedSafetyRulesCommand;
import com.vanguardtech.storepulse.platform.safety.domain.model.valueobjects.ComparisonOperator;
import com.vanguardtech.storepulse.platform.safety.domain.model.valueobjects.DeviceStatus;
import com.vanguardtech.storepulse.platform.safety.domain.model.valueobjects.ResponseAction;
import com.vanguardtech.storepulse.platform.safety.domain.model.valueobjects.SensorType;
import com.vanguardtech.storepulse.platform.safety.domain.model.valueobjects.Threshold;
import com.vanguardtech.storepulse.platform.safety.domain.services.SafetyRuleCommandService;
import com.vanguardtech.storepulse.platform.safety.infrastructure.persistence.jpa.repositories.SafetyRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SafetyRuleCommandServiceImpl implements SafetyRuleCommandService {

    private final SafetyRuleRepository safetyRuleRepository;

    @Override
    @Transactional
    public void handle(SeedSafetyRulesCommand command) {
        List.of(
                        alertRule("Motion detected", SensorType.MOTION, 1.0, ComparisonOperator.EQUAL),
                        alertRule("Smoke detected", SensorType.SMOKE, 300.0, ComparisonOperator.GREATER_THAN),
                        alertRule("High temperature", SensorType.TEMPERATURE, 45.0, ComparisonOperator.GREATER_THAN),
                        alertRule("High humidity", SensorType.HUMIDITY, 70.0, ComparisonOperator.GREATER_THAN),
                        alertRule("High electricity consumption", SensorType.ELECTRICITY, 5.0, ComparisonOperator.GREATER_THAN),
                        alertRule("High water consumption", SensorType.WATER, 0.5, ComparisonOperator.GREATER_THAN))
                .stream()
                .filter(rule -> !safetyRuleRepository.existsByName(rule.getName()))
                .forEach(safetyRuleRepository::save);
    }

    private SafetyRule alertRule(String name, SensorType sensorType, Double value, ComparisonOperator operator) {
        return new SafetyRule(name, sensorType, new Threshold(value, operator), DeviceStatus.ALERT, ResponseAction.SEND_ALERT);
    }
}
