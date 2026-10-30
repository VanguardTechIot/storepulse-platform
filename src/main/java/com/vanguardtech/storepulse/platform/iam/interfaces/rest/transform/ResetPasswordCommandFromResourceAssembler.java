package com.vanguardtech.storepulse.platform.iam.interfaces.rest.transform;

import com.vanguardtech.storepulse.platform.iam.domain.model.commands.ResetPasswordCommand;
import com.vanguardtech.storepulse.platform.iam.interfaces.rest.resources.ResetPasswordResource;

/** Converts the password reset body into its command. */
public class ResetPasswordCommandFromResourceAssembler {

    /**
     * @param resource password reset body
     * @return the equivalent command
     */
    public static ResetPasswordCommand toCommandFromResource(ResetPasswordResource resource) {
        return new ResetPasswordCommand(resource.email(), resource.code(), resource.newPassword());
    }
}
