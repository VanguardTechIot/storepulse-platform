package com.vanguardtech.storepulse.platform.iam.interfaces.rest.resources;

/**
 * Answer of sign-up and sign-in: the account and the token for the following requests. It has the
 * same shape as {@code UserSession} in the web application.
 *
 * @param user        the authenticated account
 * @param accessToken token to send as {@code Authorization: Bearer <token>}
 */
public record AuthenticatedUserResource(UserAccountResource user, String accessToken) {
}
