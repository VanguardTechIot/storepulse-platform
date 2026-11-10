package com.vanguardtech.storepulse.platform.subscriptionspayments.interfaces.rest.transform;

import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.aggregates.SubscriptionPlan;
import com.vanguardtech.storepulse.platform.subscriptionspayments.interfaces.rest.resources.SubscriptionPlanResource;

public class SubscriptionPlanResourceFromEntityAssembler {

    public static SubscriptionPlanResource toResourceFromEntity(SubscriptionPlan plan) {
        return new SubscriptionPlanResource(plan.getId(), plan.getCode().name(), plan.getMonitoredUnitLimit(),
                plan.getMonthlyPrice(), plan.getAnnualPrice(), plan.getCurrency(), plan.getFeatures(),
                plan.isPublished());
    }
}