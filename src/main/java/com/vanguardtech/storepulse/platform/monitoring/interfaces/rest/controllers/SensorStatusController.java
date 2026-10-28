package com.vanguardtech.storepulse.platform.monitoring.interfaces.rest.controllers;

import com.vanguardtech.storepulse.platform.monitoring.domain.model.queries.GetSensorStatusByDeviceIdQuery;
import com.vanguardtech.storepulse.platform.monitoring.domain.services.SensorStatusQueryService;
import com.vanguardtech.storepulse.platform.monitoring.interfaces.rest.resources.SensorStatusResource;
import com.vanguardtech.storepulse.platform.monitoring.interfaces.rest.transform.SensorStatusResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/sensors", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Sensors", description = "Current status of the sensors of each device.")
public class SensorStatusController {

    private final SensorStatusQueryService sensorStatusQueryService;

    public SensorStatusController(SensorStatusQueryService sensorStatusQueryService) {
        this.sensorStatusQueryService = sensorStatusQueryService;
    }

    @Operation(summary = "Get the latest reading of each sensor of a device")
    @GetMapping("/status")
    public ResponseEntity<List<SensorStatusResource>> getSensorStatus(@RequestParam Long deviceId) {
        var statuses = sensorStatusQueryService.handle(new GetSensorStatusByDeviceIdQuery(deviceId));
        return ResponseEntity.ok(statuses.stream()
                .map(SensorStatusResourceFromEntityAssembler::toResourceFromEntity)
                .toList());
    }
}
