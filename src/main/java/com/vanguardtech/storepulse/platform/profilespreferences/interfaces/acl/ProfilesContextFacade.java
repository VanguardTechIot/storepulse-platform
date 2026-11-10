package com.vanguardtech.storepulse.platform.profilespreferences.interfaces.acl;

import java.util.Optional;

/** Entry point of Profiles and Preferences for the other Bounded Contexts. */
public interface ProfilesContextFacade {

    /**
     * @param userId identifier of the account
     * @return first and last name of the account's profile, if it has one
     */
    Optional<String> fetchFullNameByUserId(Long userId);
}