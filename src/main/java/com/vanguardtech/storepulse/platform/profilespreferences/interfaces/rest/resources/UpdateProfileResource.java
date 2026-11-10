package com.vanguardtech.storepulse.platform.profilespreferences.interfaces.rest.resources;

/**
 * Body of {@code PUT /users/{userId}/profile}.
 *
 * <p>It has no Bean Validation constraints on purpose: the domain validates every field and answers
 * the codes the web application translates ({@code invalid_name}, {@code invalid_email},
 * {@code invalid_phone_number}, {@code unsupported_photo_format}) instead of a generic field message.
 *
 * @param firstName   first name
 * @param lastName    last name
 * @param email       sign-in email
 * @param phoneNumber contact phone; empty or {@code null} removes it
 * @param photoUrl    link to the photo; empty or {@code null} removes it
 */
public record UpdateProfileResource(String firstName, String lastName, String email, String phoneNumber,
                                    String photoUrl) {
}
