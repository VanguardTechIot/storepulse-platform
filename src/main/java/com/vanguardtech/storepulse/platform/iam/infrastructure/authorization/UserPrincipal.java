package com.vanguardtech.storepulse.platform.iam.infrastructure.authorization;

import com.vanguardtech.storepulse.platform.iam.domain.model.aggregates.UserAccount;
import com.vanguardtech.storepulse.platform.iam.domain.model.valueobjects.Role;

/**
 * Authenticated user of the current request, as stored in the Spring Security context. Controllers
 * receive it with {@code @AuthenticationPrincipal}.
 *
 * @param userId identifier of the account
 * @param email  sign-in email of the account
 * @param role   role of the account
 */
public record UserPrincipal(Long userId, String email, Role role) {

    /**
     * @param userAccount authenticated account
     * @return the principal that represents it
     */
    public static UserPrincipal from(UserAccount userAccount) {
        return new UserPrincipal(userAccount.getId(), userAccount.getEmail().address(), userAccount.getRole());
    }

    /** @return the Spring Security authority of the role, for example {@code ROLE_GALLERY_ADMINISTRATOR} */
    public String authority() {
        return "ROLE_" + role.name();
    }
}
