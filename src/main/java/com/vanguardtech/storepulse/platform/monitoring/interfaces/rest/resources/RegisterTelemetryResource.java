package com.vanguardtech.storepulse.platform.monitoring.interfaces.rest.resources;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.Instant;
import java.util.List;

public record RegisterTelemetryResource(
        @NotNull @Positive Long deviceId,
        Instant receivedAt,
        @NotEmpty List<@Valid RegisterMeasurementResource> measurements) {
}
