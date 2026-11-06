package com.vanguardtech.storepulse.platform.subscriptionspayments.domain.repositories;

import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.aggregates.SubscriptionPlan;

import java.util.List;
import java.util.Optional;

public interface SubscriptionPlanRepository {

    SubscriptionPlan save(SubscriptionPlan plan);

    Optional<SubscriptionPlan> findById(Long id);

    long count();

    List<SubscriptionPlan> findByPublishedTrueOrderByIdAsc();
}