package com.vanguardtech.storepulse.platform.iam.domain.model.commands;

/**
 * Ends the session of the authenticated user.
 *
 * @param userId identifier of the account that signs out
 */
public record SignOutCommand(Long userId) {
}
