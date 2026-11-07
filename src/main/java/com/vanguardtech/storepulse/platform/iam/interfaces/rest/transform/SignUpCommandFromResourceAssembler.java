package com.vanguardtech.storepulse.platform.iam.interfaces.rest.transform;

import com.vanguardtech.storepulse.platform.iam.domain.model.commands.SignUpCommand;
import com.vanguardtech.storepulse.platform.iam.interfaces.rest.resources.SignUpResource;

/** Converts the sign-up body into its command. */
public class SignUpCommandFromResourceAssembler {

    /**
     * @param resource sign-up body
     * @return the equivalent command
     */
    public static SignUpCommand toCommandFromResource(SignUpResource resource) {
        return new SignUpCommand(resource.fullName(), resource.email(), resource.password());
    }
}
