package com.vanguardtech.storepulse.platform.monitoring.interfaces.rest.controllers;

import com.vanguardtech.storepulse.platform.monitoring.domain.model.queries.GetAllConsumptionQuery;
import com.vanguardtech.storepulse.platform.monitoring.domain.model.queries.GetConsumptionByResourceIdQuery;
import com.vanguardtech.storepulse.platform.monitoring.domain.services.ConsumptionQueryService;
import com.vanguardtech.storepulse.platform.monitoring.interfaces.rest.resources.ConsumptionResource;
import com.vanguardtech.storepulse.platform.monitoring.interfaces.rest.transform.ConsumptionResourceFromEntityAssembler;
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
@RequestMapping(value = "/consumption", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Consumption", description = "Electricity and water consumption per period.")
public class ConsumptionController {

    private final ConsumptionQueryService consumptionQueryService;

    public ConsumptionController(ConsumptionQueryService consumptionQueryService) {
        this.consumptionQueryService = consumptionQueryService;
    }

    @Operation(summary = "Get consumption per period, optionally filtered by resource")
    @GetMapping
    public ResponseEntity<List<ConsumptionResource>> getConsumption(@RequestParam(required = false) Long resourceId) {
        var consumption = resourceId == null
                ? consumptionQueryService.handle(new GetAllConsumptionQuery())
                : consumptionQueryService.handle(new GetConsumptionByResourceIdQuery(resourceId));
        return ResponseEntity.ok(consumption.stream()
                .map(ConsumptionResourceFromEntityAssembler::toResourceFromEntity)
                .toList());
    }
}
