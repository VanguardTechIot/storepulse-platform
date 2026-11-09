package com.vanguardtech.storepulse.platform.profilespreferences.interfaces.rest.resources;

/**
 * Profile as {@code GET /users/{userId}/profile} returns it, the same shape as
 * {@code ProfileResource} in the web application.
 *
 * @param id          identifier of the profile
 * @param userId      identifier of the account that owns the profile
 * @param firstName   first name
 * @param lastName    last name
 * @param email       sign-in email
 * @param phoneNumber contact phone, or {@code null}
 * @param photoUrl    link to the photo, or {@code null} when the default image is shown
 */
public record ProfileResource(Long id, Long userId, String firstName, String lastName, String email,
                              String phoneNumber, String photoUrl) {
}