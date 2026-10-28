package com.vanguardtech.storepulse.platform.subscriptionspayments.domain.services;

import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.aggregates.Payment;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.aggregates.Subscription;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.aggregates.SubscriptionPlan;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.queries.GetAllSubscriptionPlansQuery;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.queries.GetPaymentsBySubscriptionIdQuery;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.queries.GetSubscriptionByGalleryIdQuery;

import java.util.List;
import java.util.Optional;

public interface SubscriptionQueryService {

    List<SubscriptionPlan> handle(GetAllSubscriptionPlansQuery query);

    Optional<Subscription> handle(GetSubscriptionByGalleryIdQuery query);

    /** Most recent first. */
    List<Payment> handle(GetPaymentsBySubscriptionIdQuery query);
}