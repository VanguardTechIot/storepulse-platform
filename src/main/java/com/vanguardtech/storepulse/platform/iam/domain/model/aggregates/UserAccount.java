package com.vanguardtech.storepulse.platform.iam.domain.model.aggregates;

import com.vanguardtech.storepulse.platform.iam.domain.model.events.PasswordChangedEvent;
import com.vanguardtech.storepulse.platform.iam.domain.model.events.PasswordResetEvent;
import com.vanguardtech.storepulse.platform.iam.domain.model.events.SessionEndedEvent;
import com.vanguardtech.storepulse.platform.iam.domain.model.events.UserRegisteredEvent;
import com.vanguardtech.storepulse.platform.iam.domain.model.events.UserSignedInEvent;
import com.vanguardtech.storepulse.platform.iam.domain.model.valueobjects.Email;
import com.vanguardtech.storepulse.platform.iam.domain.model.valueobjects.PasswordHash;
import com.vanguardtech.storepulse.platform.iam.domain.model.valueobjects.Role;
import com.vanguardtech.storepulse.platform.iam.domain.model.valueobjects.UserId;
import com.vanguardtech.storepulse.platform.iam.domain.model.valueobjects.UserStatus;
import com.vanguardtech.storepulse.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Access account of a StorePulse user (Aggregate Root).
 *
 * <p>It holds only what is needed to authenticate and authorize: the sign-in email, the password
 * hash, the role and the status. Profile data such as the name or the photo belongs to Profiles and
 * Preferences, not to this context.
 */
@Entity
@Table(name = "user_accounts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserAccount extends AuditableAbstractAggregateRoot<UserAccount> {

    @Embedded
    @AttributeOverride(name = "address", column = @Column(name = "email", nullable = false, unique = true))
    private Email email;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "password_hash", nullable = false))
    private PasswordHash passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    private Instant lastSignedInAt;

    /**
     * Creates an active account.
     *
     * @param email        sign-in email
     * @param passwordHash hash of the password
     * @param role         role granted to the account
     */
    public UserAccount(Email email, PasswordHash passwordHash, Role role) {
        if (email == null) throw new IllegalArgumentException("Email is required");
        if (passwordHash == null) throw new IllegalArgumentException("Password hash is required");
        if (role == null) throw new IllegalArgumentException("Role is required");
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.status = UserStatus.ACTIVE;
    }

    /**
     * Announces the registration once the account has its identifier, so other Bounded Contexts
     * can reference it.
     *
     * @param fullName full name typed in the registration, used by Profiles and Preferences
     * @param at       moment of the registration
     * @throws IllegalStateException when the account has not been persisted yet
     */
    public void confirmRegistration(String fullName, Instant at) {
        registerEvent(new UserRegisteredEvent(requirePersistedId(), email.address(), fullName, role, at));
    }

    /**
     * Records a successful authentication.
     *
     * @param at moment of the sign-in
     */
    public void signIn(Instant at) {
        this.lastSignedInAt = at;
        registerEvent(new UserSignedInEvent(requirePersistedId(), at));
    }

    /**
     * Records the end of the session.
     *
     * @param at moment of the sign-out
     */
    public void signOut(Instant at) {
        registerEvent(new SessionEndedEvent(requirePersistedId(), at));
    }

    /**
     * Records that the owner asked to recover the password.
     *
     * @param at moment of the request
     */
    public void requestPasswordReset(Instant at) {
        registerEvent(new PasswordResetEvent(requirePersistedId(), email.address(), at));
    }

    /**
     * Replaces the password.
     *
     * @param newPasswordHash hash of the new password
     * @param at              moment of the change
     */
    public void changePassword(PasswordHash newPasswordHash, Instant at) {
        if (newPasswordHash == null) throw new IllegalArgumentException("Password hash is required");
        this.passwordHash = newPasswordHash;
        registerEvent(new PasswordChangedEvent(requirePersistedId(), at));
    }

    /**
     * Replaces the sign-in email.
     *
     * @param newEmail new sign-in email
     */
    public void changeEmail(Email newEmail) {
        if (newEmail == null) throw new IllegalArgumentException("Email is required");
        this.email = newEmail;
    }

    /** @return the identifier of the account as a value object */
    public UserId getUserId() {
        return new UserId(requirePersistedId());
    }

    /** @return {@code true} when the account may sign in */
    public boolean isActive() {
        return status == UserStatus.ACTIVE;
    }

    /** @return {@code true} when the account belongs to a gallery administrator */
    public boolean isGalleryAdministrator() {
        return role == Role.GALLERY_ADMINISTRATOR;
    }

    private Long requirePersistedId() {
        if (getId() == null) throw new IllegalStateException("The user account has not been persisted yet");
        return getId();
    }
}
