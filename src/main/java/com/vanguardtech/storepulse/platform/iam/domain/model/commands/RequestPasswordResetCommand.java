package com.vanguardtech.storepulse.platform.iam.domain.model.commands;

/**
 * Asks for a recovery code to be sent to the email of an account (US-03).
 *
 * @param email email of the account whose password is forgotten
 */
public record RequestPasswordResetCommand(String email) {
}
