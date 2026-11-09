package com.vanguardtech.storepulse.platform.iam.domain.model.commands;

import com.vanguardtech.storepulse.platform.iam.domain.model.valueobjects.Role;

/**
 * Registers a gallery administrator or a tenant. The full name travels with the registration so the
 * profile can be created in Profiles and Preferences; this context does not keep it.
 *
 * @param fullName full name of the user
 * @param email    sign-in email of the new account
 * @param password plain password, hashed before it is stored
 * @param role     role granted to the account
 */
public record SignUpCommand(String fullName, String email, String password, Role role) {
    public SignUpCommand {
        if (role == null) throw new IllegalArgumentException("Role is required");
    }
}