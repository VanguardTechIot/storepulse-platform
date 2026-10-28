package com.vanguardtech.storepulse.platform.subscriptionspayments.interfaces.rest.transform;

import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.aggregates.Subscription;
import com.vanguardtech.storepulse.platform.subscriptionspayments.interfaces.rest.resources.SubscriptionResource;

public class SubscriptionResourceFromEntityAssembler {

    public static SubscriptionResource toResourceFromEntity(Subscription subscription) {
        var period = subscription.getBillingPeriod();
        return new SubscriptionResource(subscription.getId(), subscription.getGalleryId(),
                subscription.getGalleryAdministratorId(), subscription.getPlanId(), subscription.getStatus().name(),
                subscription.getBillingCycle().name(), period == null ? null : period.startDate(),
                period == null ? null : period.endDate(), subscription.getMonitoredUnitCount(),
                subscription.isAutoRenewal(), subscription.getPaymentMethodLabel());
    }
}