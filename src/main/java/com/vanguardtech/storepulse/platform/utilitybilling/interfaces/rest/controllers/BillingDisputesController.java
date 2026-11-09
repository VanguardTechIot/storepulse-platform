package com.vanguardtech.storepulse.platform.utilitybilling.interfaces.rest.controllers;

import com.vanguardtech.storepulse.platform.shared.domain.exceptions.ResourceNotFoundException;
import com.vanguardtech.storepulse.platform.utilitybilling.application.internal.outboundservices.acl.ExternalIamService;
import com.vanguardtech.storepulse.platform.utilitybilling.application.internal.outboundservices.acl.ExternalPropertyService;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.exceptions.UtilityBillingError;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.aggregates.UtilityBill;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.commands.ResolveBillingDisputeCommand;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.commands.ReviewBillingDisputeCommand;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.queries.GetBillingDisputeByIdQuery;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.queries.GetBillingDisputesByBillIdsQuery;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.queries.GetUtilityBillsByUnitIdsQuery;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.services.BillingDisputeCommandService;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.services.BillingDisputeQueryService;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.services.UtilityBillQueryService;
import com.vanguardtech.storepulse.platform.utilitybilling.interfaces.rest.resources.BillingDisputeResource;
import com.vanguardtech.storepulse.platform.utilitybilling.interfaces.rest.resources.RaiseBillingDisputeResource;
import com.vanguardtech.storepulse.platform.utilitybilling.interfaces.rest.resources.ResolveBillingDisputeResource;
import com.vanguardtech.storepulse.platform.utilitybilling.interfaces.rest.resources.ReviewBillingDisputeResource;
import com.vanguardtech.storepulse.platform.utilitybilling.interfaces.rest.transform.BillingDisputeResourceFromEntityAssembler;
import com.vanguardtech.storepulse.platform.utilitybilling.interfaces.rest.transform.RaiseBillingDisputeCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/billing-disputes")
@RequiredArgsConstructor
@Tag(name = "Billing Disputes")
public class BillingDisputesController {

    private final BillingDisputeCommandService billingDisputeCommandService;
    private final BillingDisputeQueryService billingDisputeQueryService;
    private final UtilityBillQueryService utilityBillQueryService;
    private final ExternalPropertyService externalPropertyService;
    private final ExternalIamService externalIamService;

    @Operation(summary = "Get the disputes over the bills the authenticated user may see, optionally only those of a bill")
    @GetMapping
    public List<BillingDisputeResource> getBillingDisputes(@RequestParam(required = false) Long utilityBillId) {
        var billIds = visibleBillIds().stream()
                .filter(visible -> utilityBillId == null || visible.equals(utilityBillId))
                .toList();
        if (billIds.isEmpty())
            return List.of();
        return billingDisputeQueryService.handle(new GetBillingDisputesByBillIdsQuery(billIds)).stream().map(BillingDisputeResourceFromEntityAssembler::toResourceFromEntity).toList();
    }

    @Operation(summary = "Get a dispute over a bill the authenticated user may see")
    @GetMapping("/{billingDisputeId}")
    public BillingDisputeResource getBillingDisputeById(@PathVariable Long billingDisputeId) {
        var billIds = visibleBillIds();
        return billingDisputeQueryService.handle(new GetBillingDisputeByIdQuery(billingDisputeId))
                .filter(dispute -> billIds.contains(dispute.getUtilityBillId()))
                .map(BillingDisputeResourceFromEntityAssembler::toResourceFromEntity)
                .orElseThrow(() -> new ResourceNotFoundException(UtilityBillingError.NOT_FOUND));
    }

    @Operation(summary = "Raise a dispute over an issued bill as the authenticated tenant")
    @PostMapping
    @PreAuthorize("hasRole('TENANT')")
    @ResponseStatus(HttpStatus.CREATED)
    public BillingDisputeResource raiseBillingDispute(@Valid @RequestBody RaiseBillingDisputeResource resource) {
        var command = RaiseBillingDisputeCommandFromResourceAssembler.toCommandFromResource(resource);
        return BillingDisputeResourceFromEntityAssembler.toResourceFromEntity(billingDisputeCommandService.handle(command));
    }

    @Operation(summary = "Move an open dispute to review")
    @PatchMapping("/{billingDisputeId}/review")
    @PreAuthorize("hasRole('GALLERY_ADMINISTRATOR')")
    public BillingDisputeResource reviewBillingDispute(@PathVariable Long billingDisputeId,
                                                       @Valid @RequestBody ReviewBillingDisputeResource resource) {
        return BillingDisputeResourceFromEntityAssembler.toResourceFromEntity(billingDisputeCommandService.handle(
                new ReviewBillingDisputeCommand(billingDisputeId, externalIamService.fetchAuthenticatedAdministratorId(),
                        resource.reviewNotes())));
    }

    @Operation(summary = "Resolve a dispute; the bill is resolved once none of its disputes remains open")
    @PatchMapping("/{billingDisputeId}/resolve")
    @PreAuthorize("hasRole('GALLERY_ADMINISTRATOR')")
    public BillingDisputeResource resolveBillingDispute(@PathVariable Long billingDisputeId,
                                                        @Valid @RequestBody ResolveBillingDisputeResource resource) {
        return BillingDisputeResourceFromEntityAssembler.toResourceFromEntity(billingDisputeCommandService.handle(
                new ResolveBillingDisputeCommand(billingDisputeId, externalIamService.fetchAuthenticatedAdministratorId(),
                        resource.resolution())));
    }

    private List<Long> visibleBillIds() {
        var unitIds = externalPropertyService.fetchUnitIdsVisibleToAuthenticatedUser();
        if (unitIds.isEmpty())
            return List.of();
        return utilityBillQueryService.handle(new GetUtilityBillsByUnitIdsQuery(unitIds)).stream()
                .map(UtilityBill::getId)
                .toList();
    }
}