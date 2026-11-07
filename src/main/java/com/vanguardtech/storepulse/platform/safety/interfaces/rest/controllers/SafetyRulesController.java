package com.vanguardtech.storepulse.platform.safety.interfaces.rest.controllers;

import com.vanguardtech.storepulse.platform.safety.domain.model.queries.GetAllSafetyRulesQuery;
import com.vanguardtech.storepulse.platform.safety.domain.model.queries.GetSafetyRuleByIdQuery;
import com.vanguardtech.storepulse.platform.safety.domain.services.SafetyRuleQueryService;
import com.vanguardtech.storepulse.platform.safety.interfaces.rest.resources.SafetyRuleResource;
import com.vanguardtech.storepulse.platform.safety.interfaces.rest.transform.SafetyRuleResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/safety-rules", produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Safety Rules", description = "Rules that define the state and response of each sensor reading.")
public class SafetyRulesController {

    private final SafetyRuleQueryService safetyRuleQueryService;

    @Operation(summary = "Get all safety rules")
    @GetMapping
    public ResponseEntity<List<SafetyRuleResource>> getSafetyRules() {
        return ResponseEntity.ok(safetyRuleQueryService.handle(new GetAllSafetyRulesQuery()).stream()
                .map(SafetyRuleResourceFromEntityAssembler::toResourceFromEntity)
                .toList());
    }

    @Operation(summary = "Get a safety rule by ID")
    @GetMapping("/{safetyRuleId}")
    public ResponseEntity<SafetyRuleResource> getSafetyRuleById(@PathVariable Long safetyRuleId) {
        return safetyRuleQueryService.handle(new GetSafetyRuleByIdQuery(safetyRuleId))
                .map(SafetyRuleResourceFromEntityAssembler::toResourceFromEntity)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
