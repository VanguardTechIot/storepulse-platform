package com.vanguardtech.storepulse.platform.iam.interfaces.rest.transform;

import com.vanguardtech.storepulse.platform.iam.domain.model.valueobjects.UserSession;
import com.vanguardtech.storepulse.platform.iam.interfaces.rest.resources.AuthenticatedUserResource;

/** Converts a user session into the answer of sign-up and sign-in. */
public class AuthenticatedUserResourceFromEntityAssembler {

    /**
     * @param userSession authenticated account and its access token
     * @return the answer sent to the client
     */
    public static AuthenticatedUserResource toResourceFromEntity(UserSession userSession) {
        return new AuthenticatedUserResource(
                UserAccountResourceFromEntityAssembler.toResourceFromEntity(userSession.user()),
                userSession.accessToken());
    }
}
