package com.vanguardtech.storepulse.platform.utilitybilling.domain.model.valueobjects;

/** Identifier of a billing dispute. */
public record BillingDisputeId(Long value) {

    public BillingDisputeId {
        if (value == null || value <= 0)
            throw new IllegalArgumentException("Billing dispute id must be a positive number");
    }
}