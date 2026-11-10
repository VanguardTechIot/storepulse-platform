package com.vanguardtech.storepulse.platform.monitoring.interfaces.rest.resources;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.Instant;
import java.util.List;

public record RegisterTelemetryResource(
        @NotNull @Positive Long deviceId,
        Instant receivedAt,
        @PositiveOrZero Double supplyVoltage,
        @PositiveOrZero Integer bufferedEvents,
        @NotEmpty List<@Valid RegisterMeasurementResource> measurements) {
}
