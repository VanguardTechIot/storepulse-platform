package com.vanguardtech.storepulse.platform.profilespreferences.domain.services;

import com.vanguardtech.storepulse.platform.profilespreferences.domain.model.aggregates.Profile;
import com.vanguardtech.storepulse.platform.profilespreferences.domain.model.commands.CreateProfileCommand;
import com.vanguardtech.storepulse.platform.profilespreferences.domain.model.commands.UpdateProfileCommand;

/** Use cases that change profiles. */
public interface ProfileCommandService {

    /**
     * Creates the profile of a newly registered account.
     *
     * @param command account and its registration data
     * @return the new profile
     * @throws com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException
     *         {@code profile_already_exists}, {@code invalid_name} or {@code invalid_email}
     */
    Profile handle(CreateProfileCommand command);

    /**
     * Replaces the data of a profile. When the email changes, the sign-in email of the account
     * changes with it in the same transaction.
     *
     * @param command account and its new profile data
     * @return the updated profile
     * @throws com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException
     *         {@code profile_not_found}, {@code invalid_name}, {@code invalid_email},
     *         {@code invalid_phone_number}, {@code unsupported_photo_format} or
     *         {@code email_already_registered}
     */
    Profile handle(UpdateProfileCommand command);
}
