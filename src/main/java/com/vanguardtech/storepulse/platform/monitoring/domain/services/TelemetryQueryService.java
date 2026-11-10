package com.vanguardtech.storepulse.platform.monitoring.domain.services;

import com.vanguardtech.storepulse.platform.monitoring.domain.model.aggregates.Telemetry;
import com.vanguardtech.storepulse.platform.monitoring.domain.model.queries.GetTelemetryByDeviceIdsQuery;
import com.vanguardtech.storepulse.platform.monitoring.domain.model.queries.GetTelemetryByIdQuery;

import java.util.List;
import java.util.Optional;

public interface TelemetryQueryService {
    List<Telemetry> handle(GetTelemetryByDeviceIdsQuery query);

    Optional<Telemetry> handle(GetTelemetryByIdQuery query);
}