package com.vanguardtech.storepulse.platform.monitoring.interfaces.rest.controllers;

import com.vanguardtech.storepulse.platform.monitoring.domain.model.queries.GetAllTelemetryQuery;
import com.vanguardtech.storepulse.platform.monitoring.domain.model.queries.GetTelemetryByDeviceIdQuery;
import com.vanguardtech.storepulse.platform.monitoring.domain.model.queries.GetTelemetryByIdQuery;
import com.vanguardtech.storepulse.platform.monitoring.domain.services.TelemetryCommandService;
import com.vanguardtech.storepulse.platform.monitoring.domain.services.TelemetryQueryService;
import com.vanguardtech.storepulse.platform.monitoring.interfaces.rest.resources.RegisterTelemetryResource;
import com.vanguardtech.storepulse.platform.monitoring.interfaces.rest.resources.TelemetryResource;
import com.vanguardtech.storepulse.platform.monitoring.interfaces.rest.transform.RegisterTelemetryCommandFromResourceAssembler;
import com.vanguardtech.storepulse.platform.monitoring.interfaces.rest.transform.TelemetryResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/telemetry", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Telemetry", description = "Telemetry received from the IoT devices.")
public class TelemetryController {

    private final TelemetryCommandService telemetryCommandService;
    private final TelemetryQueryService telemetryQueryService;

    public TelemetryController(TelemetryCommandService telemetryCommandService,
                               TelemetryQueryService telemetryQueryService) {
        this.telemetryCommandService = telemetryCommandService;
        this.telemetryQueryService = telemetryQueryService;
    }

    @Operation(summary = "Register telemetry sent by a device through the edge gateway")
    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<TelemetryResource> registerTelemetry(@Valid @RequestBody RegisterTelemetryResource resource) {
        var command = RegisterTelemetryCommandFromResourceAssembler.toCommandFromResource(resource);
        var telemetry = telemetryCommandService.handle(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TelemetryResourceFromEntityAssembler.toResourceFromEntity(telemetry));
    }

    @Operation(summary = "Get telemetry, optionally filtered by device")
    @GetMapping
    public ResponseEntity<List<TelemetryResource>> getTelemetry(@RequestParam(required = false) Long deviceId) {
        var telemetry = deviceId == null
                ? telemetryQueryService.handle(new GetAllTelemetryQuery())
                : telemetryQueryService.handle(new GetTelemetryByDeviceIdQuery(deviceId));
        return ResponseEntity.ok(telemetry.stream()
                .map(TelemetryResourceFromEntityAssembler::toResourceFromEntity)
                .toList());
    }

    @Operation(summary = "Get telemetry by ID")
    @GetMapping("/{telemetryId}")
    public ResponseEntity<TelemetryResource> getTelemetryById(@PathVariable Long telemetryId) {
        return telemetryQueryService.handle(new GetTelemetryByIdQuery(telemetryId))
                .map(TelemetryResourceFromEntityAssembler::toResourceFromEntity)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
