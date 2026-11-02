package com.vanguardtech.storepulse.platform.safety.domain.services;

import com.vanguardtech.storepulse.platform.safety.domain.model.aggregates.Alert;
import com.vanguardtech.storepulse.platform.safety.domain.model.commands.AcknowledgeAlertCommand;
import com.vanguardtech.storepulse.platform.safety.domain.model.commands.EvaluateMeasurementCommand;
import com.vanguardtech.storepulse.platform.safety.domain.model.commands.ResolveAlertCommand;
import com.vanguardtech.storepulse.platform.safety.domain.model.valueobjects.MeasurementAssessment;

public interface AlertCommandService {
    MeasurementAssessment handle(EvaluateMeasurementCommand command);

    Alert handle(AcknowledgeAlertCommand command);

    Alert handle(ResolveAlertCommand command);
}
