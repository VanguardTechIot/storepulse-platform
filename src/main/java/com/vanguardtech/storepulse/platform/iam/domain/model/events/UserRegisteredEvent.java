package com.vanguardtech.storepulse.platform.iam.domain.model.events;

import com.vanguardtech.storepulse.platform.iam.domain.model.valueobjects.Role;
import com.vanguardtech.storepulse.platform.shared.domain.model.events.DomainEvent;

import java.time.Instant;

/**
 * A new account was registered (US-01). It carries the full name typed in the registration so
 * Profiles and Preferences can create the profile; Identity and Access Management does not keep it.
 *
 * @param userId     identifier of the new account
 * @param email      sign-in email of the account
 * @param fullName   full name typed in the registration
 * @param role       role granted to the account
 * @param occurredOn moment of the registration
 */
public record UserRegisteredEvent(Long userId, String email, String fullName, Role role, Instant occurredOn)
        implements DomainEvent {
}
