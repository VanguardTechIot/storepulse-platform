package com.vanguardtech.storepulse.platform.subscriptionspayments.interfaces.rest.controllers;

import com.vanguardtech.storepulse.platform.shared.domain.exceptions.ResourceNotFoundException;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.exceptions.SubscriptionsError;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.commands.CancelRenewalCommand;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.commands.ReactivateRenewalCommand;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.queries.GetPaymentsBySubscriptionIdQuery;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.queries.GetSubscriptionByGalleryIdQuery;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.services.SubscriptionCommandService;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.services.SubscriptionQueryService;
import com.vanguardtech.storepulse.platform.subscriptionspayments.interfaces.rest.resources.PaymentResource;
import com.vanguardtech.storepulse.platform.subscriptionspayments.interfaces.rest.resources.RequestSubscriptionResource;
import com.vanguardtech.storepulse.platform.subscriptionspayments.interfaces.rest.resources.SubscriptionResource;
import com.vanguardtech.storepulse.platform.subscriptionspayments.interfaces.rest.transform.PaymentResourceFromEntityAssembler;
import com.vanguardtech.storepulse.platform.subscriptionspayments.interfaces.rest.transform.RequestSubscriptionCommandFromResourceAssembler;
import com.vanguardtech.storepulse.platform.subscriptionspayments.interfaces.rest.transform.SubscriptionResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
@Tag(name = "Subscriptions")
public class SubscriptionsController {

    private final SubscriptionCommandService subscriptionCommandService;
    private final SubscriptionQueryService subscriptionQueryService;

    @GetMapping
    public SubscriptionResource getSubscriptionByGalleryId(@RequestParam Long galleryId) {
        return subscriptionQueryService.handle(new GetSubscriptionByGalleryIdQuery(galleryId))
                .map(SubscriptionResourceFromEntityAssembler::toResourceFromEntity)
                .orElseThrow(() -> new ResourceNotFoundException(SubscriptionsError.SUBSCRIPTION_NOT_FOUND));
    }

    /** Contracts or changes the plan and charges it; answers 409 {@code payment_rejected} when the card is declined. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SubscriptionResource requestSubscription(@Valid @RequestBody RequestSubscriptionResource resource) {
        var command = RequestSubscriptionCommandFromResourceAssembler.toCommandFromResource(resource);
        return SubscriptionResourceFromEntityAssembler.toResourceFromEntity(subscriptionCommandService.handle(command));
    }

    @PostMapping("/{subscriptionId}/cancel-renewal")
    public SubscriptionResource cancelRenewal(@PathVariable Long subscriptionId) {
        return SubscriptionResourceFromEntityAssembler.toResourceFromEntity(
                subscriptionCommandService.handle(new CancelRenewalCommand(subscriptionId)));
    }

    @PostMapping("/{subscriptionId}/reactivate-renewal")
    public SubscriptionResource reactivateRenewal(@PathVariable Long subscriptionId) {
        return SubscriptionResourceFromEntityAssembler.toResourceFromEntity(
                subscriptionCommandService.handle(new ReactivateRenewalCommand(subscriptionId)));
    }

    @GetMapping("/{subscriptionId}/payments")
    public List<PaymentResource> getPaymentsBySubscriptionId(@PathVariable Long subscriptionId) {
        return subscriptionQueryService.handle(new GetPaymentsBySubscriptionIdQuery(subscriptionId)).stream()
                .map(PaymentResourceFromEntityAssembler::toResourceFromEntity).toList();
    }
}