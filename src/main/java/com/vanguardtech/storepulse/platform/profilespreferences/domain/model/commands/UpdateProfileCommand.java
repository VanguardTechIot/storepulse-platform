package com.vanguardtech.storepulse.platform.profilespreferences.domain.model.commands;

/**
 * Replaces the personal and contact data of the profile and its photo (US-07), as
 * {@code PUT /users/{userId}/profile} receives them. The email is also the sign-in email, so
 * changing it changes how the user signs in.
 *
 * @param userId      identifier of the account that owns the profile
 * @param firstName   first name
 * @param lastName    last name
 * @param email       sign-in email
 * @param phoneNumber contact phone; empty or {@code null} removes it
 * @param photoUrl    link to the photo; empty or {@code null} removes it
 */
public record UpdateProfileCommand(Long userId, String firstName, String lastName, String email,
                                   String phoneNumber, String photoUrl) {
}
