package com.vanguardtech.storepulse.platform.iam.domain.model.events;

import com.vanguardtech.storepulse.platform.shared.domain.model.events.DomainEvent;

import java.time.Instant;

/**
 * A user authenticated successfully and received an access token (US-02).
 *
 * @param userId     identifier of the account
 * @param occurredOn moment of the sign-in
 */
public record UserSignedInEvent(Long userId, Instant occurredOn) implements DomainEvent {
}
