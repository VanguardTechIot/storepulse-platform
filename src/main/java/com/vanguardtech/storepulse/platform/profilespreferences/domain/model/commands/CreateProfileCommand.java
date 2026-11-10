package com.vanguardtech.storepulse.platform.profilespreferences.domain.model.commands;

/**
 * Creates the profile of a newly registered user.
 *
 * @param userId    identifier of the account that owns the profile
 * @param firstName first name
 * @param lastName  last name, possibly empty
 * @param email     sign-in email of the account
 */
public record CreateProfileCommand(Long userId, String firstName, String lastName, String email) {

    /**
     * Builds the command from the full name typed in the registration: the first word becomes the
     * first name and the rest the last name. The user can correct both in My Profile.
     *
     * @param userId   identifier of the account
     * @param fullName full name typed in the registration
     * @param email    sign-in email of the account
     * @return the command for the new profile
     */
    public static CreateProfileCommand fromFullName(Long userId, String fullName, String email) {
        var names = fullName == null ? new String[0] : fullName.trim().split("\\s+", 2);
        var firstName = names.length > 0 ? names[0] : "";
        var lastName = names.length > 1 ? names[1] : "";
        return new CreateProfileCommand(userId, firstName, lastName, email);
    }
}
