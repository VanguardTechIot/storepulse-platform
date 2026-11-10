package com.vanguardtech.storepulse.platform.subscriptionspayments.infrastructure.persistence.jpa.repositories;

import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.aggregates.Subscription;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.repositories.SubscriptionRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionJpaRepository extends JpaRepository<Subscription, Long>, SubscriptionRepository {
}