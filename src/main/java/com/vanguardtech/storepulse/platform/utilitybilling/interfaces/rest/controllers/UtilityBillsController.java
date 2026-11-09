package com.vanguardtech.storepulse.platform.utilitybilling.interfaces.rest.controllers;

import com.vanguardtech.storepulse.platform.shared.domain.exceptions.ResourceNotFoundException;
import com.vanguardtech.storepulse.platform.utilitybilling.application.internal.outboundservices.acl.ExternalIamService;
import com.vanguardtech.storepulse.platform.utilitybilling.application.internal.outboundservices.acl.ExternalPropertyService;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.exceptions.UtilityBillingError;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.aggregates.UtilityBill;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.commands.IssueUtilityBillCommand;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.commands.VerifyBillingDataCommand;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.queries.GetUtilityBillByIdQuery;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.queries.GetUtilityBillsByUnitIdsQuery;
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
    private final ExternalPropertyService externalPropertyService;
    private final ExternalIamService externalIamService;

    @Operation(summary = "Get the bills the authenticated user may see, most recent first, optionally only those of a unit",
            description = "An administrator sees the bills of the gallery units; a tenant, those of the store they occupy.")
    @GetMapping
    public List<UtilityBillResource> getUtilityBills(@RequestParam(required = false) Long unitId) {
        var unitIds = externalPropertyService.fetchUnitIdsVisibleToAuthenticatedUser().stream()
                .filter(visible -> unitId == null || visible.equals(unitId))
                .toList();
        var bills = unitIds.isEmpty() ? List.<UtilityBill>of()
                : utilityBillQueryService.handle(new GetUtilityBillsByUnitIdsQuery(unitIds));
        return bills.stream().map(UtilityBillResourceFromEntityAssembler::toResourceFromEntity).toList();
    }

    @Operation(summary = "Get a bill the authenticated user may see, with its breakdown")
    @GetMapping("/{utilityBillId}")
    public UtilityBillResource getUtilityBillById(@PathVariable Long utilityBillId) {
        var unitIds = externalPropertyService.fetchUnitIdsVisibleToAuthenticatedUser();
        return utilityBillQueryService.handle(new GetUtilityBillByIdQuery(utilityBillId))
                .filter(bill -> unitIds.contains(bill.getUnitId()))
                .map(UtilityBillResourceFromEntityAssembler::toResourceFromEntity)
                .orElseThrow(() -> new ResourceNotFoundException(UtilityBillingError.NOT_FOUND));
    }

    @Operation(summary = "Generate the draft bill of a store of the administrator's gallery for a month",
            description = "Answers 404 unit_not_found, 409 unit_not_monitored when the store has no linked resource, "
                    + "duplicate when it already has a bill for the month and no_consumption_data when Monitoring has no record.")
    @PostMapping
    @PreAuthorize("hasRole('GALLERY_ADMINISTRATOR')")
    @ResponseStatus(HttpStatus.CREATED)
    public UtilityBillResource generateUtilityBill(@Valid @RequestBody GenerateUtilityBillResource resource) {
        var command = GenerateUtilityBillCommandFromResourceAssembler.toCommandFromResource(resource,
                externalIamService.fetchAuthenticatedAdministratorId());
        return UtilityBillResourceFromEntityAssembler.toResourceFromEntity(utilityBillCommandService.handle(command));
    }

    @Operation(summary = "Verify the data of a draft bill",
            description = "Incomplete data needs acceptIncompleteData; otherwise answers 409 incomplete_data_not_accepted.")
    @PatchMapping("/{utilityBillId}/verify-data")
    @PreAuthorize("hasRole('GALLERY_ADMINISTRATOR')")
    public UtilityBillResource verifyBillingData(@PathVariable Long utilityBillId,
                                                 @RequestBody VerifyBillingDataResource resource) {
        return UtilityBillResourceFromEntityAssembler.toResourceFromEntity(utilityBillCommandService.handle(
                new VerifyBillingDataCommand(utilityBillId, externalIamService.fetchAuthenticatedAdministratorId(),
                        resource.acceptIncompleteData())));
    }

    @Operation(summary = "Issue a verified bill; it is due 15 days later")
    @PatchMapping("/{utilityBillId}/issue")
    @PreAuthorize("hasRole('GALLERY_ADMINISTRATOR')")
    public UtilityBillResource issueUtilityBill(@PathVariable Long utilityBillId) {
        return UtilityBillResourceFromEntityAssembler.toResourceFromEntity(
                utilityBillCommandService.handle(new IssueUtilityBillCommand(utilityBillId,
                        externalIamService.fetchAuthenticatedAdministratorId())));
    }
}