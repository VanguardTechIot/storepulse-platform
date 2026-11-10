package com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.queries;

import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.valueobjects.GalleryId;

public record GetSubscriptionByGalleryIdQuery(GalleryId galleryId) {
}