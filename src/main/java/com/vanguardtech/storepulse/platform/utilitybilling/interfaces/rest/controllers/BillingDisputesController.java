package com.vanguardtech.storepulse.platform.utilitybilling.interfaces.rest.controllers;

import com.vanguardtech.storepulse.platform.shared.domain.exceptions.ResourceNotFoundException;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.exceptions.UtilityBillingError;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.commands.ResolveBillingDisputeCommand;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.commands.ReviewBillingDisputeCommand;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.queries.GetAllBillingDisputesQuery;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.queries.GetBillingDisputeByIdQuery;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.queries.GetBillingDisputesByBillIdQuery;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.services.BillingDisputeCommandService;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.services.BillingDisputeQueryService;
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

    @Operation(summary = "Get the disputes, optionally only those of a bill")
    @GetMapping
    public List<BillingDisputeResource> getBillingDisputes(@RequestParam(required = false) Long utilityBillId) {
        var disputes = utilityBillId == null
                ? billingDisputeQueryService.handle(new GetAllBillingDisputesQuery())
                : billingDisputeQueryService.handle(new GetBillingDisputesByBillIdQuery(utilityBillId));
        return disputes.stream().map(BillingDisputeResourceFromEntityAssembler::toResourceFromEntity).toList();
    }

    @Operation(summary = "Get a dispute")
    @GetMapping("/{billingDisputeId}")
    public BillingDisputeResource getBillingDisputeById(@PathVariable Long billingDisputeId) {
        return billingDisputeQueryService.handle(new GetBillingDisputeByIdQuery(billingDisputeId))
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
                new ReviewBillingDisputeCommand(billingDisputeId, resource.reviewNotes())));
    }

    @Operation(summary = "Resolve a dispute; the bill is resolved once none of its disputes remains open")
    @PatchMapping("/{billingDisputeId}/resolve")
    @PreAuthorize("hasRole('GALLERY_ADMINISTRATOR')")
    public BillingDisputeResource resolveBillingDispute(@PathVariable Long billingDisputeId,
                                                        @Valid @RequestBody ResolveBillingDisputeResource resource) {
        return BillingDisputeResourceFromEntityAssembler.toResourceFromEntity(billingDisputeCommandService.handle(
                new ResolveBillingDisputeCommand(billingDisputeId, resource.resolution())));
    }
}