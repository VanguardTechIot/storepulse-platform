package com.vanguardtech.storepulse.platform.monitoring.interfaces.rest.controllers;

import com.vanguardtech.storepulse.platform.monitoring.application.internal.outboundservices.acl.ExternalResourceAssetService;
import com.vanguardtech.storepulse.platform.monitoring.domain.model.queries.GetTelemetryByDeviceIdsQuery;
import com.vanguardtech.storepulse.platform.monitoring.domain.model.queries.GetTelemetryByIdQuery;
import com.vanguardtech.storepulse.platform.monitoring.domain.services.TelemetryCommandService;
import com.vanguardtech.storepulse.platform.monitoring.domain.services.TelemetryQueryService;
import com.vanguardtech.storepulse.platform.monitoring.interfaces.rest.resources.RegisterTelemetryResource;
import com.vanguardtech.storepulse.platform.monitoring.interfaces.rest.resources.TelemetryRegistrationResource;
import com.vanguardtech.storepulse.platform.monitoring.interfaces.rest.resources.TelemetryResource;
import com.vanguardtech.storepulse.platform.monitoring.interfaces.rest.transform.RegisterTelemetryCommandFromResourceAssembler;
import com.vanguardtech.storepulse.platform.monitoring.interfaces.rest.transform.TelemetryRegistrationResourceFromResultAssembler;
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
    private final ExternalResourceAssetService externalResourceAssetService;

    public TelemetryController(TelemetryCommandService telemetryCommandService,
                               TelemetryQueryService telemetryQueryService,
                               ExternalResourceAssetService externalResourceAssetService) {
        this.telemetryCommandService = telemetryCommandService;
        this.telemetryQueryService = telemetryQueryService;
        this.externalResourceAssetService = externalResourceAssetService;
    }

    @Operation(summary = "Register telemetry sent by a device through the edge gateway")
    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<TelemetryRegistrationResource> registerTelemetry(@Valid @RequestBody RegisterTelemetryResource resource) {
        var command = RegisterTelemetryCommandFromResourceAssembler.toCommandFromResource(resource);
        var registration = telemetryCommandService.handle(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TelemetryRegistrationResourceFromResultAssembler.toResourceFromResult(registration));
    }

    @Operation(summary = "Get the telemetry of the devices visible to the user, optionally of one device",
            description = "The administrator sees the devices of the gallery and the tenant the ones of the store.")
    @GetMapping
    public ResponseEntity<List<TelemetryResource>> getTelemetry(@RequestParam(required = false) Long deviceId) {
        var deviceIds = externalResourceAssetService.fetchDeviceIdsVisibleToAuthenticatedUser().stream()
                .filter(id -> deviceId == null || id.equals(deviceId))
                .toList();
        return ResponseEntity.ok(telemetryQueryService.handle(new GetTelemetryByDeviceIdsQuery(deviceIds)).stream()
                .map(TelemetryResourceFromEntityAssembler::toResourceFromEntity)
                .toList());
    }

    @Operation(summary = "Get telemetry by ID", description = "Answers 404 when its device is not visible to the user.")
    @GetMapping("/{telemetryId}")
    public ResponseEntity<TelemetryResource> getTelemetryById(@PathVariable Long telemetryId) {
        var deviceIds = externalResourceAssetService.fetchDeviceIdsVisibleToAuthenticatedUser();
        return telemetryQueryService.handle(new GetTelemetryByIdQuery(telemetryId))
                .filter(telemetry -> deviceIds.contains(telemetry.getDeviceId()))
                .map(TelemetryResourceFromEntityAssembler::toResourceFromEntity)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}