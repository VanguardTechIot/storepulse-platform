package com.vanguardtech.storepulse.platform.iam.domain.model.queries;

/**
 * Looks up an account by its sign-in email. The email is normalized before the search.
 *
 * @param email sign-in email of the account
 */
public record GetUserAccountByEmailQuery(String email) {
}
