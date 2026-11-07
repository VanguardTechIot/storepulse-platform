package com.vanguardtech.storepulse.platform.iam.interfaces.rest.transform;

import com.vanguardtech.storepulse.platform.iam.domain.model.commands.RequestPasswordResetCommand;
import com.vanguardtech.storepulse.platform.iam.interfaces.rest.resources.RequestPasswordResetResource;

/** Converts the password recovery request body into its command. */
public class RequestPasswordResetCommandFromResourceAssembler {

    /**
     * @param resource password recovery request body
     * @return the equivalent command
     */
    public static RequestPasswordResetCommand toCommandFromResource(RequestPasswordResetResource resource) {
        return new RequestPasswordResetCommand(resource.email());
    }
}
