package com.vanguardtech.storepulse.platform.iam.domain.model.events;

import com.vanguardtech.storepulse.platform.shared.domain.model.events.DomainEvent;

import java.time.Instant;

/**
 * A sign-in was rejected and recorded for auditing.
 *
 * @param email      email typed in the attempt
 * @param userId     identifier of the account that email belongs to, or {@code null} when none does
 * @param reason     error code of the rejection, for example {@code invalid_credentials}
 * @param occurredOn moment of the attempt
 */
public record UnauthorizedAccessEvent(String email, Long userId, String reason, Instant occurredOn)
        implements DomainEvent {
}
