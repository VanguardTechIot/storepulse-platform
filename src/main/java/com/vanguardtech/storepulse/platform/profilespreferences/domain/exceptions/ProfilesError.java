package com.vanguardtech.storepulse.platform.profilespreferences.domain.exceptions;

/**
 * Business outcomes that prevent a profile operation from completing.
 *
 * <p>The lowercase name of each value is the error code answered by the REST API, the same key the
 * web application translates as {@code profiles.errors.<code>} (see {@code ProfilesErrorCode}).
 * {@code email_already_registered} is answered by Identity and Access Management, which owns the
 * sign-in email.
 */
public enum ProfilesError {
    /** A name is empty or longer than {@code Profile.NAME_MAX_LENGTH} characters. */
    INVALID_NAME,
    /** The email is missing or malformed. */
    INVALID_EMAIL,
    /** The phone number does not have the supported format. */
    INVALID_PHONE_NUMBER,
    /** The photo is not a JPG or PNG image served over HTTP(S). */
    UNSUPPORTED_PHOTO_FORMAT,
    /** The user has no profile. */
    PROFILE_NOT_FOUND,
    /** The user already has a profile. */
    PROFILE_ALREADY_EXISTS
}
