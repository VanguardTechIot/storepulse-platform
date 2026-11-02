package com.vanguardtech.storepulse.platform.monitoring.domain.services;

import com.vanguardtech.storepulse.platform.monitoring.domain.model.commands.RegisterTelemetryCommand;
import com.vanguardtech.storepulse.platform.monitoring.domain.model.valueobjects.TelemetryRegistration;

public interface TelemetryCommandService {
    TelemetryRegistration handle(RegisterTelemetryCommand command);
}
