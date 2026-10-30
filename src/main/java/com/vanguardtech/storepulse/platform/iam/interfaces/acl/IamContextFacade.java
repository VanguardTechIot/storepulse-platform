package com.vanguardtech.storepulse.platform.iam.interfaces.acl;

import java.util.Optional;

/**
 * Entry point of Identity and Access Management for the other Bounded Contexts. It exposes only
 * primitive values, so no other context depends on the IAM domain model.
 */
public interface IamContextFacade {

    /**
     * @return the identifier of the user authenticated in the current request, or empty when the
     * request is anonymous
     */
    Optional<Long> fetchAuthenticatedUserId();

    /**
     * @param email sign-in email, in any letter case
     * @return the identifier of the account that uses the email, if any
     */
    Optional<Long> fetchUserIdByEmail(String email);

    /**
     * @param userId identifier of the account
     * @return the sign-in email of the account, if it exists
     */
    Optional<String> fetchEmailByUserId(Long userId);

    /**
     * @param userId identifier of the account
     * @return {@code true} when the account exists
     */
    boolean existsUserById(Long userId);

    /**
     * Used by Property Management and Subscriptions and Payments, where only the gallery
     * administrator may act.
     *
     * @param userId identifier of the account
     * @return {@code true} when the account exists, is active and belongs to a gallery administrator
     */
    boolean isActiveGalleryAdministrator(Long userId);

    /**
     * Keeps the sign-in email equal to the profile email when Profiles and Preferences changes it.
     *
     * @param userId identifier of the account
     * @param email  new sign-in email
     * @throws com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException
     *         {@code user_not_found} or {@code email_already_registered}
     */
    void updateUserEmail(Long userId, String email);
}
