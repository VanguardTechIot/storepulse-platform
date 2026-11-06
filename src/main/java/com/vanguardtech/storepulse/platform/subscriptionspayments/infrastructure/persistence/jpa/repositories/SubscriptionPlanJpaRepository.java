package com.vanguardtech.storepulse.platform.subscriptionspayments.infrastructure.persistence.jpa.repositories;

import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.aggregates.SubscriptionPlan;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.repositories.SubscriptionPlanRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionPlanJpaRepository extends JpaRepository<SubscriptionPlan, Long>, SubscriptionPlanRepository {
}