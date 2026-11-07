package com.vanguardtech.storepulse.platform.iam.interfaces.rest.transform;

import com.vanguardtech.storepulse.platform.iam.domain.model.aggregates.UserAccount;
import com.vanguardtech.storepulse.platform.iam.interfaces.rest.resources.UserAccountResource;

/** Converts a user account into its public representation. */
public class UserAccountResourceFromEntityAssembler {

    /**
     * @param userAccount account to expose
     * @return its public representation, without the password hash
     */
    public static UserAccountResource toResourceFromEntity(UserAccount userAccount) {
        return new UserAccountResource(userAccount.getId(), userAccount.getEmail().address(),
                userAccount.getRole().name(), userAccount.getStatus().name());
    }
}
