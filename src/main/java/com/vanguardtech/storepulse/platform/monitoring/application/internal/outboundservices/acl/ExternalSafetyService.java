package com.vanguardtech.storepulse.platform.monitoring.application.internal.outboundservices.acl;

import com.vanguardtech.storepulse.platform.monitoring.domain.model.aggregates.Telemetry;
import com.vanguardtech.storepulse.platform.monitoring.domain.model.entities.Measurement;
import com.vanguardtech.storepulse.platform.monitoring.domain.model.valueobjects.MeasurementEvaluation;
import com.vanguardtech.storepulse.platform.safety.interfaces.acl.SafetyContextFacade;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExternalSafetyService {

    private final SafetyContextFacade safetyContextFacade;

    public ExternalSafetyService(SafetyContextFacade safetyContextFacade) {
        this.safetyContextFacade = safetyContextFacade;
    }

    public List<MeasurementEvaluation> evaluate(Telemetry telemetry) {
        return telemetry.getMeasurements().stream()
                .map(measurement -> evaluate(telemetry.getDeviceId(), measurement))
                .toList();
    }

    private MeasurementEvaluation evaluate(Long deviceId, Measurement measurement) {
        var evaluation = safetyContextFacade.evaluate(
                deviceId, measurement.getId(), measurement.getType().name(), measurement.getValue().value(),
                measurement.getRecordedAt());
        return new MeasurementEvaluation(
                measurement.getType(), evaluation.deviceStatus(), evaluation.response(), evaluation.alertTriggered());
    }
}
