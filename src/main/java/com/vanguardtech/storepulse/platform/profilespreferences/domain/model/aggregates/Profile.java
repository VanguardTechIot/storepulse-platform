package com.vanguardtech.storepulse.platform.profilespreferences.domain.model.aggregates;

import com.vanguardtech.storepulse.platform.profilespreferences.domain.exceptions.ProfilesError;
import com.vanguardtech.storepulse.platform.profilespreferences.domain.model.valueobjects.EmailAddress;
import com.vanguardtech.storepulse.platform.profilespreferences.domain.model.valueobjects.PhoneNumber;
import com.vanguardtech.storepulse.platform.profilespreferences.domain.model.valueobjects.ProfilePhoto;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException;
import com.vanguardtech.storepulse.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Personal data of a StorePulse user (Aggregate Root): name, contact data and photo (US-06, US-07).
 * The role and the credentials belong to Identity and Access Management; an account has exactly one
 * profile.
 *
 * <p>Every value is validated before anything changes, so invalid data never alters the current
 * profile (US-07, scenario 2).
 */
@Entity
@Table(name = "profiles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Profile extends AuditableAbstractAggregateRoot<Profile> {

    /** Maximum number of characters of the first name and of the last name. */
    public static final int NAME_MAX_LENGTH = 50;

    @Column(nullable = false, unique = true)
    private Long userId;

    @Column(nullable = false, length = NAME_MAX_LENGTH)
    private String firstName;

    @Column(nullable = false, length = NAME_MAX_LENGTH)
    private String lastName;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "email", nullable = false))
    private EmailAddress email;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "phone_number"))
    private PhoneNumber phoneNumber;

    @Embedded
    @AttributeOverride(name = "url", column = @Column(name = "photo_url", length = 2048))
    private ProfilePhoto photo;

    /**
     * Creates the profile of a newly registered account, without phone or photo. The last name may
     * be empty because the registration asks only for a full name.
     *
     * @param userId    identifier of the account
     * @param firstName first name
     * @param lastName  last name, possibly empty
     * @param email     sign-in email of the account
     * @throws BusinessRuleException {@code invalid_name} when a name is not valid
     */
    public Profile(Long userId, String firstName, String lastName, EmailAddress email) {
        if (userId == null || userId <= 0) throw new IllegalArgumentException("User ID must be a positive number");
        if (email == null) throw new BusinessRuleException(ProfilesError.INVALID_EMAIL);
        if (!isValidName(firstName) || lastName == null || lastName.trim().length() > NAME_MAX_LENGTH)
            throw new BusinessRuleException(ProfilesError.INVALID_NAME);
        this.userId = userId;
        this.firstName = firstName.trim();
        this.lastName = lastName.trim();
        this.email = email;
    }

    /**
     * Replaces the personal and contact data and the photo (US-07).
     *
     * @param firstName   first name
     * @param lastName    last name
     * @param email       sign-in email
     * @param phoneNumber contact phone, or {@code null} to remove it
     * @param photo       photo, or {@code null} to show the default image
     * @throws BusinessRuleException {@code invalid_name} or {@code invalid_email}; nothing changes
     */
    public void update(String firstName, String lastName, EmailAddress email, PhoneNumber phoneNumber,
                       ProfilePhoto photo) {
        if (!isValidName(firstName) || !isValidName(lastName)) throw new BusinessRuleException(ProfilesError.INVALID_NAME);
        if (email == null) throw new BusinessRuleException(ProfilesError.INVALID_EMAIL);
        this.firstName = firstName.trim();
        this.lastName = lastName.trim();
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.photo = photo;
    }

    /**
     * Tells whether a name is not empty and fits the column.
     *
     * @param name name to validate
     * @return {@code true} when the trimmed name has between 1 and {@value #NAME_MAX_LENGTH} characters
     */
    public static boolean isValidName(String name) {
        if (name == null) return false;
        var trimmed = name.trim();
        return !trimmed.isEmpty() && trimmed.length() <= NAME_MAX_LENGTH;
    }
}
