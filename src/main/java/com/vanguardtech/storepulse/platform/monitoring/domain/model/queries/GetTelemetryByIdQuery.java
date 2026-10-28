package com.vanguardtech.storepulse.platform.monitoring.domain.model.queries;

public record GetTelemetryByIdQuery(Long telemetryId) {
    public GetTelemetryByIdQuery {
        if (telemetryId == null || telemetryId <= 0)
            throw new IllegalArgumentException("Telemetry ID must be a positive number");
    }
}
