package com.vanguardtech.storepulse.platform.subscriptionspayments.domain.repositories;

import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.aggregates.Subscription;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.valueobjects.GalleryId;

import java.util.Optional;

public interface SubscriptionRepository {

    Subscription save(Subscription subscription);

    Optional<Subscription> findById(Long id);

    Optional<Subscription> findByGalleryId(GalleryId galleryId);
}