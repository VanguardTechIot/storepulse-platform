package com.vanguardtech.storepulse.platform.iam.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

/**
 * One-way hash of a password. The plain password is never stored: it is hashed by the
 * {@code PasswordHasher} outbound service before it reaches the aggregate.
 *
 * @param value the encoded hash, including its algorithm parameters
 */
@Embeddable
public record PasswordHash(String value) {

    /**
     * Validates the hash.
     *
     * @throws IllegalArgumentException when the hash is missing
     */
    public PasswordHash {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("Password hash is required");
    }

    /** Hides the hash so it never ends up in logs. */
    @Override
    public String toString() {
        return "PasswordHash[****]";
    }
}
