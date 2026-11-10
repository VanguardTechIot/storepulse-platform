package com.vanguardtech.storepulse.platform.profilespreferences.domain.services;

import com.vanguardtech.storepulse.platform.profilespreferences.domain.model.aggregates.Profile;
import com.vanguardtech.storepulse.platform.profilespreferences.domain.model.queries.GetProfileByUserIdQuery;

import java.util.Optional;

/** Use cases that read profiles. */
public interface ProfileQueryService {

    /**
     * @param query account that owns the profile
     * @return the profile, or empty when the account has none
     */
    Optional<Profile> handle(GetProfileByUserIdQuery query);
}
