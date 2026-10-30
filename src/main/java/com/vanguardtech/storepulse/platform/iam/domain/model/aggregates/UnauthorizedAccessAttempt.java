package com.vanguardtech.storepulse.platform.iam.domain.model.aggregates;

import com.vanguardtech.storepulse.platform.iam.domain.exceptions.IamError;
import com.vanguardtech.storepulse.platform.iam.domain.model.events.UnauthorizedAccessAttemptLoggedEvent;
import com.vanguardtech.storepulse.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Rejected sign-in kept for auditing (Aggregate Root). Immutable once recorded.
 *
 * <p>The typed email is stored as it arrived, even when no account uses it, because those are
 * precisely the attempts worth reviewing.
 */
@Entity
@Table(name = "unauthorized_access_attempts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UnauthorizedAccessAttempt extends AuditableAbstractAggregateRoot<UnauthorizedAccessAttempt> {

    @Column(nullable = false)
    private String email;

    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IamError reason;

    @Column(nullable = false)
    private Instant attemptedAt;

    /**
     * Records a rejected sign-in.
     *
     * @param email       email typed in the attempt
     * @param userId      account that email belongs to, or {@code null} when none does
     * @param reason      why the sign-in was rejected
     * @param attemptedAt moment of the attempt
     */
    public UnauthorizedAccessAttempt(String email, Long userId, IamError reason, Instant attemptedAt) {
        if (email == null || email.isBlank()) throw new IllegalArgumentException("Email is required");
        if (reason == null) throw new IllegalArgumentException("Reason is required");
        if (attemptedAt == null) throw new IllegalArgumentException("Attempt date is required");
        this.email = email;
        this.userId = userId;
        this.reason = reason;
        this.attemptedAt = attemptedAt;
        registerEvent(new UnauthorizedAccessAttemptLoggedEvent(email, userId, reason.name().toLowerCase(),
                attemptedAt));
    }
}
