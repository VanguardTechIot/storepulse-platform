package com.vanguardtech.storepulse.platform.utilitybilling.interfaces.rest.controllers;

import com.vanguardtech.storepulse.platform.shared.domain.exceptions.ResourceNotFoundException;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.exceptions.UtilityBillingError;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.commands.IssueUtilityBillCommand;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.commands.VerifyBillingDataCommand;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.queries.GetAllUtilityBillsQuery;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.queries.GetUtilityBillByIdQuery;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.queries.GetUtilityBillsByUnitIdQuery;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.services.UtilityBillCommandService;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.services.UtilityBillQueryService;
import com.vanguardtech.storepulse.platform.utilitybilling.interfaces.rest.resources.GenerateUtilityBillResource;
import com.vanguardtech.storepulse.platform.utilitybilling.interfaces.rest.resources.UtilityBillResource;
import com.vanguardtech.storepulse.platform.utilitybilling.interfaces.rest.resources.VerifyBillingDataResource;
import com.vanguardtech.storepulse.platform.utilitybilling.interfaces.rest.transform.GenerateUtilityBillCommandFromResourceAssembler;
import com.vanguardtech.storepulse.platform.utilitybilling.interfaces.rest.transform.UtilityBillResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/utility-bills")
@RequiredArgsConstructor
@Tag(name = "Utility Bills")
public class UtilityBillsController {

    private final UtilityBillCommandService utilityBillCommandService;
    private final UtilityBillQueryService utilityBillQueryService;

    @Operation(summary = "Get the bills, optionally only those of a unit")
    @GetMapping
    public List<UtilityBillResource> getUtilityBills(@RequestParam(required = false) Long unitId) {
        var bills = unitId == null
                ? utilityBillQueryService.handle(new GetAllUtilityBillsQuery())
                : utilityBillQueryService.handle(new GetUtilityBillsByUnitIdQuery(unitId));
        return bills.stream().map(UtilityBillResourceFromEntityAssembler::toResourceFromEntity).toList();
    }

    @Operation(summary = "Get a bill with its breakdown")
    @GetMapping("/{utilityBillId}")
    public UtilityBillResource getUtilityBillById(@PathVariable Long utilityBillId) {
        return utilityBillQueryService.handle(new GetUtilityBillByIdQuery(utilityBillId))
                .map(UtilityBillResourceFromEntityAssembler::toResourceFromEntity)
                .orElseThrow(() -> new ResourceNotFoundException(UtilityBillingError.NOT_FOUND));
    }

    @Operation(summary = "Generate the draft bill of a unit for a month from its recorded consumption",
            description = "Answers 409 duplicate when the unit already has a bill for the month and no_consumption_data when Monitoring has no record.")
    @PostMapping
    @PreAuthorize("hasRole('GALLERY_ADMINISTRATOR')")
    @ResponseStatus(HttpStatus.CREATED)
    public UtilityBillResource generateUtilityBill(@Valid @RequestBody GenerateUtilityBillResource resource) {
        var command = GenerateUtilityBillCommandFromResourceAssembler.toCommandFromResource(resource);
        return UtilityBillResourceFromEntityAssembler.toResourceFromEntity(utilityBillCommandService.handle(command));
    }

    @Operation(summary = "Verify the data of a draft bill",
            description = "Incomplete data needs acceptIncompleteData; otherwise answers 409 incomplete_data_not_accepted.")
    @PatchMapping("/{utilityBillId}/verify-data")
    @PreAuthorize("hasRole('GALLERY_ADMINISTRATOR')")
    public UtilityBillResource verifyBillingData(@PathVariable Long utilityBillId,
                                                 @RequestBody VerifyBillingDataResource resource) {
        return UtilityBillResourceFromEntityAssembler.toResourceFromEntity(utilityBillCommandService.handle(
                new VerifyBillingDataCommand(utilityBillId, resource.acceptIncompleteData())));
    }

    @Operation(summary = "Issue a verified bill; it is due 15 days later")
    @PatchMapping("/{utilityBillId}/issue")
    @PreAuthorize("hasRole('GALLERY_ADMINISTRATOR')")
    public UtilityBillResource issueUtilityBill(@PathVariable Long utilityBillId) {
        return UtilityBillResourceFromEntityAssembler.toResourceFromEntity(
                utilityBillCommandService.handle(new IssueUtilityBillCommand(utilityBillId)));
    }
}