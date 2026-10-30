package com.vanguardtech.storepulse.platform.iam.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

/**
 * Identifier of a {@code UserAccount}, used by the aggregates that reference an account without
 * holding it.
 *
 * @param userId the technical identifier of the account
 */
@Embeddable
public record UserId(Long userId) {

    /**
     * Validates the identifier.
     *
     * @throws IllegalArgumentException when the identifier is missing or not positive
     */
    public UserId {
        if (userId == null || userId <= 0) throw new IllegalArgumentException("User ID must be a positive number");
    }
}
