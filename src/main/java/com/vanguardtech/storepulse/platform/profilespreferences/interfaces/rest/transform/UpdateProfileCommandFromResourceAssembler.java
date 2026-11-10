package com.vanguardtech.storepulse.platform.profilespreferences.interfaces.rest.transform;

import com.vanguardtech.storepulse.platform.profilespreferences.domain.model.commands.UpdateProfileCommand;
import com.vanguardtech.storepulse.platform.profilespreferences.interfaces.rest.resources.UpdateProfileResource;

/** Converts the profile update body into its command. */
public class UpdateProfileCommandFromResourceAssembler {

    /**
     * @param userId   account that owns the profile, taken from the path
     * @param resource profile update body
     * @return the equivalent command
     */
    public static UpdateProfileCommand toCommandFromResource(Long userId, UpdateProfileResource resource) {
        return new UpdateProfileCommand(userId, resource.firstName(), resource.lastName(), resource.email(),
                resource.phoneNumber(), resource.photoUrl());
    }
}
