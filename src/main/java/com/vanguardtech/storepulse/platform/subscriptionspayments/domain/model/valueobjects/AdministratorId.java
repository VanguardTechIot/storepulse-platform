package com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/** Reference to the user of Identity and Access Management who administers the gallery. */
@Embeddable
public record AdministratorId(@Column(name = "gallery_administrator_id") Long value) {

    public AdministratorId {
        if (value == null || value <= 0)
            throw new IllegalArgumentException("Administrator ID must be a positive number");
    }
}