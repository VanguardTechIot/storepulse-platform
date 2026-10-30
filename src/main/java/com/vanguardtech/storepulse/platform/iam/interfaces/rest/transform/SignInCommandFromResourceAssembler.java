package com.vanguardtech.storepulse.platform.iam.interfaces.rest.transform;

import com.vanguardtech.storepulse.platform.iam.domain.model.commands.SignInCommand;
import com.vanguardtech.storepulse.platform.iam.interfaces.rest.resources.SignInResource;

/** Converts the sign-in body into its command. */
public class SignInCommandFromResourceAssembler {

    /**
     * @param resource sign-in body
     * @return the equivalent command
     */
    public static SignInCommand toCommandFromResource(SignInResource resource) {
        return new SignInCommand(resource.email(), resource.password());
    }
}
