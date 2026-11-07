package com.vanguardtech.storepulse.platform.iam.domain.exceptions;

/**
 * Business outcomes that prevent an identity operation from completing.
 *
 * <p>The lowercase name of each value is the error code answered by the REST API, the same key
 * the web application translates as {@code iam.errors.<code>} (see {@code IamErrorCode}).
 */
public enum IamError {
    /** The email is not registered or the password does not match. */
    INVALID_CREDENTIALS,
    /** The account exists but is disabled, so it cannot sign in. */
    ACCOUNT_INACTIVE,
    /** Another account already uses the email address. */
    EMAIL_ALREADY_REGISTERED,
    /** The password does not satisfy the password policy. */
    WEAK_PASSWORD,
    /** The recovery code does not match, was already used or was blocked after too many attempts. */
    INVALID_RESET_CODE,
    /** The recovery code matched but its validity window is over. */
    EXPIRED_RESET_CODE,
    /** The requested user account does not exist. */
    USER_NOT_FOUND
}
