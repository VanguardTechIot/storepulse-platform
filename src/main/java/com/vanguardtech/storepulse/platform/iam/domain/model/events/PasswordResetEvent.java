package com.vanguardtech.storepulse.platform.iam.domain.model.events;

import com.vanguardtech.storepulse.platform.shared.domain.model.events.DomainEvent;

import java.time.Instant;

/**
 * The owner of an account asked to recover the password (US-03).
 *
 * @param userId     identifier of the account
 * @param email      email the recovery code is sent to
 * @param occurredOn moment of the request
 */
public record PasswordResetEvent(Long userId, String email, Instant occurredOn) implements DomainEvent {
}
