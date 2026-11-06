package com.vanguardtech.storepulse.platform.subscriptionspayments.interfaces.rest.controllers;

import com.vanguardtech.storepulse.platform.shared.domain.exceptions.ResourceNotFoundException;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.exceptions.SubscriptionsError;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.commands.CancelRenewalCommand;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.commands.ReactivateRenewalCommand;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.queries.GetPaymentsBySubscriptionIdQuery;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.queries.GetSubscriptionByGalleryIdQuery;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.valueobjects.GalleryId;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.services.SubscriptionCommandService;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.services.SubscriptionQueryService;
import com.vanguardtech.storepulse.platform.subscriptionspayments.interfaces.rest.resources.PaymentResource;
import com.vanguardtech.storepulse.platform.subscriptionspayments.interfaces.rest.resources.RequestSubscriptionResource;
import com.vanguardtech.storepulse.platform.subscriptionspayments.interfaces.rest.resources.SubscriptionResource;
import com.vanguardtech.storepulse.platform.subscriptionspayments.interfaces.rest.transform.PaymentResourceFromEntityAssembler;
import com.vanguardtech.storepulse.platform.subscriptionspayments.interfaces.rest.transform.RequestSubscriptionCommandFromResourceAssembler;
import com.vanguardtech.storepulse.platform.subscriptionspayments.interfaces.rest.transform.SubscriptionResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
@Tag(name = "Subscriptions")
public class SubscriptionsController {

    private final SubscriptionCommandService subscriptionCommandService;
    private final SubscriptionQueryService subscriptionQueryService;

    @Operation(summary = "Get the subscription of a gallery")
    @GetMapping
    public SubscriptionResource getSubscriptionByGalleryId(@RequestParam Long galleryId) {
        return subscriptionQueryService.handle(new GetSubscriptionByGalleryIdQuery(new GalleryId(galleryId)))
                .map(SubscriptionResourceFromEntityAssembler::toResourceFromEntity)
                .orElseThrow(() -> new ResourceNotFoundException(SubscriptionsError.SUBSCRIPTION_NOT_FOUND));
    }

    @Operation(summary = "Contract or change the plan and charge it",
            description = "Answers 409 payment_rejected when the card is declined.")
    @PostMapping
    @PreAuthorize("hasRole('GALLERY_ADMINISTRATOR')")
    @ResponseStatus(HttpStatus.CREATED)
    public SubscriptionResource requestSubscription(@Valid @RequestBody RequestSubscriptionResource resource) {
        var command = RequestSubscriptionCommandFromResourceAssembler.toCommandFromResource(resource);
        return SubscriptionResourceFromEntityAssembler.toResourceFromEntity(subscriptionCommandService.handle(command));
    }

    @Operation(summary = "Cancel the automatic renewal")
    @PostMapping("/{subscriptionId}/cancel-renewal")
    @PreAuthorize("hasRole('GALLERY_ADMINISTRATOR')")
    public SubscriptionResource cancelRenewal(@PathVariable Long subscriptionId) {
        return SubscriptionResourceFromEntityAssembler.toResourceFromEntity(
                subscriptionCommandService.handle(new CancelRenewalCommand(subscriptionId)));
    }

    @Operation(summary = "Reactivate the automatic renewal")
    @PostMapping("/{subscriptionId}/reactivate-renewal")
    @PreAuthorize("hasRole('GALLERY_ADMINISTRATOR')")
    public SubscriptionResource reactivateRenewal(@PathVariable Long subscriptionId) {
        return SubscriptionResourceFromEntityAssembler.toResourceFromEntity(
                subscriptionCommandService.handle(new ReactivateRenewalCommand(subscriptionId)));
    }

    @Operation(summary = "Get the payments of a subscription, most recent first")
    @GetMapping("/{subscriptionId}/payments")
    public List<PaymentResource> getPaymentsBySubscriptionId(@PathVariable Long subscriptionId) {
        return subscriptionQueryService.handle(new GetPaymentsBySubscriptionIdQuery(subscriptionId)).stream()
                .map(PaymentResourceFromEntityAssembler::toResourceFromEntity).toList();
    }
}