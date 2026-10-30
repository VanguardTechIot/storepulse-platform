package com.vanguardtech.storepulse.platform.iam.domain.model.valueobjects;

import com.vanguardtech.storepulse.platform.iam.domain.model.aggregates.UserAccount;

/**
 * Result of a successful authentication: the account and the access token that authorizes its
 * requests. It is the counterpart of {@code UserSession} in the web application.
 *
 * @param user        the authenticated account
 * @param accessToken the token the client sends as {@code Authorization: Bearer <token>}
 */
public record UserSession(UserAccount user, String accessToken) {
}
