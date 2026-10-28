package com.vanguardtech.storepulse.platform.monitoring.domain.services;

import com.vanguardtech.storepulse.platform.monitoring.domain.model.aggregates.Telemetry;
import com.vanguardtech.storepulse.platform.monitoring.domain.model.commands.RegisterTelemetryCommand;

public interface TelemetryCommandService {
    Telemetry handle(RegisterTelemetryCommand command);
}
