package com.vanguardtech.storepulse.platform.profilespreferences.application.acl;

import com.vanguardtech.storepulse.platform.profilespreferences.domain.model.queries.GetProfileByUserIdQuery;
import com.vanguardtech.storepulse.platform.profilespreferences.domain.services.ProfileQueryService;
import com.vanguardtech.storepulse.platform.profilespreferences.interfaces.acl.ProfilesContextFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/** Implementation of {@link ProfilesContextFacade} on top of the profile query service. */
@Service
@RequiredArgsConstructor
public class ProfilesContextFacadeImpl implements ProfilesContextFacade {

    private final ProfileQueryService profileQueryService;

    @Override
    public Optional<String> fetchFullNameByUserId(Long userId) {
        return profileQueryService.handle(new GetProfileByUserIdQuery(userId))
                .map(profile -> (profile.getFirstName() + " " + profile.getLastName()).trim());
    }
}