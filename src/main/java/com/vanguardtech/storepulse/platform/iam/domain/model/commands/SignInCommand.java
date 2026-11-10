package com.vanguardtech.storepulse.platform.iam.domain.model.commands;

/**
 * Authenticates a user with an email and a password.
 *
 * @param email    sign-in email of the account
 * @param password plain password to verify
 */
public record SignInCommand(String email, String password) {
}