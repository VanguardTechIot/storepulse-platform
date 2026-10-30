package com.vanguardtech.storepulse.platform.iam.domain.model.commands;

/**
 * Replaces the sign-in email of an account. The sign-in email is also the profile email, so
 * Profiles and Preferences issues it when the user edits the profile.
 *
 * @param userId identifier of the account
 * @param email  new sign-in email
 */
public record ChangeUserEmailCommand(Long userId, String email) {
}
