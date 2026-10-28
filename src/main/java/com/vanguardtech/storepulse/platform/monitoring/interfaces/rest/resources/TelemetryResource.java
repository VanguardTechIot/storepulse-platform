package com.vanguardtech.storepulse.platform.monitoring.interfaces.rest.resources;

import java.time.Instant;
import java.util.List;

public record TelemetryResource(Long id, Long deviceId, Instant receivedAt, List<MeasurementResource> measurements) {
}
