package com.vanguardtech.storepulse.platform.iam.domain.model.events;

import com.vanguardtech.storepulse.platform.shared.domain.model.events.DomainEvent;

import java.time.Instant;

/**
 * The password of an account was replaced.
 *
 * @param userId     identifier of the account
 * @param occurredOn moment of the change
 */
public record PasswordChangedEvent(Long userId, Instant occurredOn) implements DomainEvent {
}
