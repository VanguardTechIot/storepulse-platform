package com.vanguardtech.storepulse.platform.iam.domain.model.events;

import com.vanguardtech.storepulse.platform.shared.domain.model.events.DomainEvent;

import java.time.Instant;

/**
 * A user signed out of the application.
 *
 * @param userId     identifier of the account
 * @param occurredOn moment of the sign-out
 */
public record SessionEndedEvent(Long userId, Instant occurredOn) implements DomainEvent {
}
