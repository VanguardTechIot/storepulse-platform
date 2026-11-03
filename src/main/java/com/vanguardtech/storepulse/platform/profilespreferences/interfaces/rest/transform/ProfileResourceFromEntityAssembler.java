package com.vanguardtech.storepulse.platform.profilespreferences.interfaces.rest.transform;

import com.vanguardtech.storepulse.platform.profilespreferences.domain.model.aggregates.Profile;
import com.vanguardtech.storepulse.platform.profilespreferences.interfaces.rest.resources.ProfileResource;

/** Converts a profile into its public representation. */
public class ProfileResourceFromEntityAssembler {

    /**
     * @param profile profile to expose
     * @return its public representation; the optional phone and photo become {@code null} when missing
     */
    public static ProfileResource toResourceFromEntity(Profile profile) {
        var phoneNumber = profile.getPhoneNumber();
        var photo = profile.getPhoto();
        return new ProfileResource(profile.getId(), profile.getUserId(), profile.getFirstName(),
                profile.getLastName(), profile.getEmail().value(),
                phoneNumber == null ? null : phoneNumber.value(),
                photo == null ? null : photo.url());
    }
}
