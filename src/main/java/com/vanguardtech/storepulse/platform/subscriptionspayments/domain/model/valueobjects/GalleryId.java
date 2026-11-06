package com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/** Reference to a gallery of Property Management; a gallery has at most one subscription. */
@Embeddable
public record GalleryId(@Column(name = "gallery_id", unique = true, nullable = false) Long value) {

    public GalleryId {
        if (value == null || value <= 0)
            throw new IllegalArgumentException("Gallery ID must be a positive number");
    }
}