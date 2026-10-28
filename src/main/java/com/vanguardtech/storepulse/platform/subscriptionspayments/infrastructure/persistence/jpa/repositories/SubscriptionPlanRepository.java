package com.vanguardtech.storepulse.platform.subscriptionspayments.infrastructure.persistence.jpa.repositories;

import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.aggregates.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {

    List<SubscriptionPlan> findByPublishedTrueOrderByIdAsc();
}