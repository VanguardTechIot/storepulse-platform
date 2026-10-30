package com.vanguardtech.storepulse.platform.iam.domain.model.queries;

/**
 * Looks up an account by its identifier.
 *
 * @param userId identifier of the account
 */
public record GetUserAccountByIdQuery(Long userId) {
}
