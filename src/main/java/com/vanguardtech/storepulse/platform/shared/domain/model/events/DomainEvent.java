package com.vanguardtech.storepulse.platform.shared.domain.model.events;
import java.time.Instant;

/**
 * Marker contract of every Domain Event published by an aggregate.
 *
 * <p>Events are named in the past tense (UserRegistered, UtilityBillIssued) and carry only the
 * data other Bounded Contexts need to react. They are immutable, so implementations are expected
 * to be Java records.
 */
public interface DomainEvent {
    /** Moment in which the event happened.*/
    Instant occurredOn();
}
