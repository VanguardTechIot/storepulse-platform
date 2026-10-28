package com.vanguardtech.storepulse.platform.subscriptionspayments.interfaces.rest.controllers;

import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.queries.GetAllSubscriptionPlansQuery;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.services.SubscriptionQueryService;
import com.vanguardtech.storepulse.platform.subscriptionspayments.interfaces.rest.resources.SubscriptionPlanResource;
import com.vanguardtech.storepulse.platform.subscriptionspayments.interfaces.rest.transform.SubscriptionPlanResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/subscription-plans")
@RequiredArgsConstructor
@Tag(name = "Subscription Plans")
public class SubscriptionPlansController {

    private final SubscriptionQueryService subscriptionQueryService;

    @GetMapping
    public List<SubscriptionPlanResource> getAllSubscriptionPlans() {
        return subscriptionQueryService.handle(new GetAllSubscriptionPlansQuery()).stream()
                .map(SubscriptionPlanResourceFromEntityAssembler::toResourceFromEntity).toList();
    }
}