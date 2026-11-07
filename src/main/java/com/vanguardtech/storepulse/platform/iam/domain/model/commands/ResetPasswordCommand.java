package com.vanguardtech.storepulse.platform.iam.domain.model.commands;

/**
 * Replaces the password of an account using the recovery code sent to its email (US-03).
 *
 * @param email       email of the account
 * @param code        six-digit recovery code
 * @param newPassword plain new password, hashed before it is stored
 */
public record ResetPasswordCommand(String email, String code, String newPassword) {
}
