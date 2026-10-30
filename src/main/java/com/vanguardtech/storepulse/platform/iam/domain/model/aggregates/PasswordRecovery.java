package com.vanguardtech.storepulse.platform.iam.domain.model.aggregates;

import com.vanguardtech.storepulse.platform.iam.domain.model.events.PasswordResetCodeGeneratedEvent;
import com.vanguardtech.storepulse.platform.iam.domain.model.valueobjects.UserId;
import com.vanguardtech.storepulse.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.Instant;

/**
 * Password recovery process of a user (Aggregate Root): a 6-digit code that is valid for 15 minutes
 * and can be used only once (US-03).
 *
 * <p>Only the hash of the code is stored. After {@value #MAX_FAILED_ATTEMPTS} wrong codes the
 * recovery is blocked, so the code cannot be guessed by trying every combination.
 */
@Entity
@Table(name = "password_recoveries")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PasswordRecovery extends AuditableAbstractAggregateRoot<PasswordRecovery> {

    /** Time during which a recovery code is accepted. */
    public static final Duration VALIDITY = Duration.ofMinutes(15);

    /** Number of wrong codes after which the recovery is blocked. */
    public static final int MAX_FAILED_ATTEMPTS = 5;

    @Embedded
    @AttributeOverride(name = "userId", column = @Column(name = "user_id", nullable = false))
    private UserId userId;

    @Column(nullable = false)
    private String codeHash;

    @Column(nullable = false)
    private Instant requestedAt;

    @Column(nullable = false)
    private Instant expiresAt;

    private int failedAttempts;
    private boolean used;

    /**
     * Starts a recovery whose code expires {@link #VALIDITY} after the request.
     *
     * @param userId      account being recovered
     * @param codeHash    hash of the code sent to the user
     * @param requestedAt moment of the request
     */
    public PasswordRecovery(UserId userId, String codeHash, Instant requestedAt) {
        if (userId == null) throw new IllegalArgumentException("User ID is required");
        if (codeHash == null || codeHash.isBlank()) throw new IllegalArgumentException("Code hash is required");
        if (requestedAt == null) throw new IllegalArgumentException("Request date is required");
        this.userId = userId;
        this.codeHash = codeHash;
        this.requestedAt = requestedAt;
        this.expiresAt = requestedAt.plus(VALIDITY);
        this.failedAttempts = 0;
        this.used = false;
        registerEvent(new PasswordResetCodeGeneratedEvent(userId.userId(), expiresAt, requestedAt));
    }

    /**
     * Tells whether the code can still be tried: it was not used and it was not blocked.
     *
     * @return {@code true} when the recovery accepts codes
     */
    public boolean isOpen() {
        return !used && failedAttempts < MAX_FAILED_ATTEMPTS;
    }

    /**
     * Tells whether the validity window is over.
     *
     * @param now moment to compare with the expiration
     * @return {@code true} when the code is no longer valid
     */
    public boolean isExpired(Instant now) {
        return !now.isBefore(expiresAt);
    }

    /** Counts a wrong code; the recovery is blocked when the limit is reached. */
    public void registerFailedAttempt() {
        failedAttempts++;
    }

    /**
     * Consumes the code, so it cannot be used again.
     *
     * @throws IllegalStateException when the recovery no longer accepts codes
     */
    public void markAsUsed() {
        if (!isOpen()) throw new IllegalStateException("The password recovery is closed");
        used = true;
    }
}
