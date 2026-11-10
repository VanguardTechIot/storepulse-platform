package com.vanguardtech.storepulse.platform.iam.interfaces.rest.resources;

/**
 * Public representation of a user account. The password hash is never exposed.
 *
 * @param id     identifier of the account
 * @param email  sign-in email
 * @param role   {@code GALLERY_ADMINISTRATOR} or {@code TENANT}
 * @param status {@code ACTIVE} or {@code INACTIVE}
 */
public record UserAccountResource(Long id, String email, String role, String status) {
}
