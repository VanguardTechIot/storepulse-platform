package com.vanguardtech.storepulse.platform.safety.application.internal.commandservices;

import com.vanguardtech.storepulse.platform.safety.application.internal.outboundservices.acl.ExternalAssetsService;
import com.vanguardtech.storepulse.platform.safety.domain.exceptions.SafetyError;
import com.vanguardtech.storepulse.platform.safety.domain.model.aggregates.Alert;
import com.vanguardtech.storepulse.platform.safety.domain.model.aggregates.DeviceState;
import com.vanguardtech.storepulse.platform.safety.domain.model.aggregates.SafetyRule;
import com.vanguardtech.storepulse.platform.safety.domain.model.commands.AcknowledgeAlertCommand;
import com.vanguardtech.storepulse.platform.safety.domain.model.commands.EvaluateMeasurementCommand;
import com.vanguardtech.storepulse.platform.safety.domain.model.commands.ResolveAlertCommand;
import com.vanguardtech.storepulse.platform.safety.domain.model.valueobjects.AlertStatus;
import com.vanguardtech.storepulse.platform.safety.domain.model.valueobjects.DeviceStatus;
import com.vanguardtech.storepulse.platform.safety.domain.model.valueobjects.MeasurementAssessment;
import com.vanguardtech.storepulse.platform.safety.domain.model.valueobjects.ResponseAction;
import com.vanguardtech.storepulse.platform.safety.domain.services.AlertCommandService;
import com.vanguardtech.storepulse.platform.safety.infrastructure.persistence.jpa.repositories.AlertRepository;
import com.vanguardtech.storepulse.platform.safety.infrastructure.persistence.jpa.repositories.DeviceStateRepository;
import com.vanguardtech.storepulse.platform.safety.infrastructure.persistence.jpa.repositories.SafetyRuleRepository;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AlertCommandServiceImpl implements AlertCommandService {

    private final AlertRepository alertRepository;
    private final DeviceStateRepository deviceStateRepository;
    private final SafetyRuleRepository safetyRuleRepository;
    private final ExternalAssetsService externalAssetsService;

    @Override
    @Transactional
    public MeasurementAssessment handle(EvaluateMeasurementCommand command) {
        var rule = findTriggeredRule(command);
        if (rule.isEmpty() || hasOpenAlert(command))
            return new MeasurementAssessment(
                    refreshDeviceState(command.deviceId(), command.recordedAt()), ResponseAction.NONE, false);
        alertRepository.save(new Alert(
                rule.get().getId(), command.deviceId(), command.measurementId(), command.sensorType(), command.value(),
                command.recordedAt()));
        return new MeasurementAssessment(
                refreshDeviceState(command.deviceId(), command.recordedAt()), rule.get().getResponseAction(), true);
    }

    @Override
    @Transactional
    public Alert handle(AcknowledgeAlertCommand command) {
        var alert = findAlert(command.alertId());
        alert.acknowledge();
        return alertRepository.save(alert);
    }

    @Override
    @Transactional
    public Alert handle(ResolveAlertCommand command) {
        var alert = findAlert(command.alertId());
        alert.resolve(command.resolutionNote());
        var saved = alertRepository.save(alert);
        refreshDeviceState(saved.getDeviceId(), saved.getResolvedAt());
        return saved;
    }

    private Optional<SafetyRule> findTriggeredRule(EvaluateMeasurementCommand command) {
        return safetyRuleRepository.findBySensorType(command.sensorType()).stream()
                .filter(rule -> rule.isTriggeredBy(command.value(), command.recordedAt()))
                .findFirst();
    }

    private boolean hasOpenAlert(EvaluateMeasurementCommand command) {
        return alertRepository.existsByDeviceIdAndSensorTypeAndStatusNot(
                command.deviceId(), command.sensorType(), AlertStatus.RESOLVED);
    }

    private DeviceStatus refreshDeviceState(Long deviceId, Instant evaluatedAt) {
        var alerted = alertRepository.existsByDeviceIdAndStatusNot(deviceId, AlertStatus.RESOLVED);
        var status = alerted ? DeviceStatus.ALERT : DeviceStatus.NORMAL;
        var response = alerted ? ResponseAction.SEND_ALERT : ResponseAction.NONE;
        var deviceState = deviceStateRepository.findByDeviceId(deviceId)
                .orElseGet(() -> new DeviceState(deviceId, status, response, evaluatedAt));
        deviceState.update(status, response, evaluatedAt);
        deviceStateRepository.save(deviceState);
        return status;
    }

    /** The alert, only when its device is visible to the authenticated user. */
    private Alert findAlert(Long alertId) {
        return alertRepository.findById(alertId)
                .filter(alert -> externalAssetsService.fetchDeviceIdsVisibleToAuthenticatedUser()
                        .contains(alert.getDeviceId()))
                .orElseThrow(() -> new ResourceNotFoundException(SafetyError.ALERT_NOT_FOUND));
    }
}