package com.vanguardtech.storepulse.platform.iam.interfaces.rest.transform;

import com.vanguardtech.storepulse.platform.iam.domain.model.commands.SignUpCommand;
import com.vanguardtech.storepulse.platform.iam.domain.model.valueobjects.Role;
import com.vanguardtech.storepulse.platform.iam.interfaces.rest.resources.SignUpResource;

/** Converts the sign-up body into its command. */
public class SignUpCommandFromResourceAssembler {

    /**
     * @param resource sign-up body
     * @return the equivalent command
     */
    public static SignUpCommand toCommandFromResource(SignUpResource resource) {
        var role = resource.role() != null ? resource.role() : Role.GALLERY_ADMINISTRATOR;
        return new SignUpCommand(resource.fullName(), resource.email(), resource.password(), role);
    }
}