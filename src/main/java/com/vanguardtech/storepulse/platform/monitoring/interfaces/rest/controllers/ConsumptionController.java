package com.vanguardtech.storepulse.platform.monitoring.interfaces.rest.controllers;

import com.vanguardtech.storepulse.platform.monitoring.application.internal.outboundservices.acl.ExternalPropertyCommunicationService;
import com.vanguardtech.storepulse.platform.monitoring.domain.model.queries.GetConsumptionByResourceIdsQuery;
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
    private final ExternalPropertyCommunicationService externalPropertyCommunicationService;

    public ConsumptionController(ConsumptionQueryService consumptionQueryService,
                                 ExternalPropertyCommunicationService externalPropertyCommunicationService) {
        this.consumptionQueryService = consumptionQueryService;
        this.externalPropertyCommunicationService = externalPropertyCommunicationService;
    }

    @Operation(summary = "Get the consumption per period of the resources visible to the user, optionally of one resource",
            description = "The administrator sees the resources of the gallery and the tenant the one of the store.")
    @GetMapping
    public ResponseEntity<List<ConsumptionResource>> getConsumption(@RequestParam(required = false) Long resourceId) {
        var resourceIds = externalPropertyCommunicationService.fetchResourceIdsVisibleToAuthenticatedUser().stream()
                .filter(id -> resourceId == null || id.equals(resourceId))
                .toList();
        return ResponseEntity.ok(consumptionQueryService.handle(new GetConsumptionByResourceIdsQuery(resourceIds)).stream()
                .map(ConsumptionResourceFromEntityAssembler::toResourceFromEntity)
                .toList());
    }
}