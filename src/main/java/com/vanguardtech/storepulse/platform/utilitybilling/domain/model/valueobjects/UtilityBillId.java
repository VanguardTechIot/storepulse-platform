package com.vanguardtech.storepulse.platform.utilitybilling.domain.model.valueobjects;

/** Identifier of a utility bill. */
public record UtilityBillId(Long value) {

    public UtilityBillId {
        if (value == null || value <= 0)
            throw new IllegalArgumentException("Utility bill id must be a positive number");
    }
}