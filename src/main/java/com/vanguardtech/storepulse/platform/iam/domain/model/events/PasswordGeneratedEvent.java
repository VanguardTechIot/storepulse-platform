package com.vanguardtech.storepulse.platform.iam.domain.model.events;

import com.vanguardtech.storepulse.platform.shared.domain.model.events.DomainEvent;

import java.time.Instant;

/**
 * A recovery code was generated for an account. The code itself is not part of the event: it only
 * travels to the user's email.
 *
 * @param userId     identifier of the account
 * @param expiresAt  moment from which the code is no longer accepted
 * @param occurredOn moment the code was generated
 */
public record PasswordGeneratedEvent(Long userId, Instant expiresAt, Instant occurredOn)
        implements DomainEvent {
}
