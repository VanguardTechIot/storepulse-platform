package com.vanguardtech.storepulse.platform.profilespreferences.application.internal.queryservices;

import com.vanguardtech.storepulse.platform.profilespreferences.domain.model.aggregates.Profile;
import com.vanguardtech.storepulse.platform.profilespreferences.domain.model.queries.GetProfileByUserIdQuery;
import com.vanguardtech.storepulse.platform.profilespreferences.domain.services.ProfileQueryService;
import com.vanguardtech.storepulse.platform.profilespreferences.infrastructure.persistence.jpa.repositories.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/** Implementation of the use cases that read profiles. */
@Service
@RequiredArgsConstructor
public class ProfileQueryServiceImpl implements ProfileQueryService {

    private final ProfileRepository profileRepository;

    @Override
    public Optional<Profile> handle(GetProfileByUserIdQuery query) {
        return profileRepository.findByUserId(query.userId());
    }
}
