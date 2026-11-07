package com.vanguardtech.storepulse.platform.iam.domain.model.commands;

/**
 * Registers a gallery administrator (US-01). The full name travels with the registration so the
 * profile can be created in Profiles and Preferences; this context does not keep it.
 *
 * @param fullName full name of the administrator
 * @param email    sign-in email of the new account
 * @param password plain password, hashed before it is stored
 */
public record SignUpCommand(String fullName, String email, String password) {
}
