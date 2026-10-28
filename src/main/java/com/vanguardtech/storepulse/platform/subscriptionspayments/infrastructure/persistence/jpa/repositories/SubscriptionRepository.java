package com.vanguardtech.storepulse.platform.subscriptionspayments.infrastructure.persistence.jpa.repositories;

import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.aggregates.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    Optional<Subscription> findByGalleryId(Long galleryId);
}