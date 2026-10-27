package com.vanguardtech.storepulse.platform.resourceassetmanagement.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

@Embeddable
public record Location(String address, String floor, String reference) {
    public Location {
        if (address == null || address.isBlank())
            throw new IllegalArgumentException("Address is required");
        if (floor == null || floor.isBlank())
            throw new IllegalArgumentException("Floor is required");
    }
}
