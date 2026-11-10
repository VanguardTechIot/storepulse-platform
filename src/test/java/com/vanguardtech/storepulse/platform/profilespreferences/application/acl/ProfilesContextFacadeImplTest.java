package com.vanguardtech.storepulse.platform.profilespreferences.application.acl;

import com.vanguardtech.storepulse.platform.profilespreferences.domain.model.aggregates.Profile;
import com.vanguardtech.storepulse.platform.profilespreferences.domain.model.queries.GetProfileByUserIdQuery;
import com.vanguardtech.storepulse.platform.profilespreferences.domain.model.valueobjects.EmailAddress;
import com.vanguardtech.storepulse.platform.profilespreferences.domain.services.ProfileQueryService;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProfilesContextFacadeImplTest {

    private final ProfileQueryService profileQueryService = mock(ProfileQueryService.class);
    private final ProfilesContextFacadeImpl facade = new ProfilesContextFacadeImpl(profileQueryService);

    @Test
    void joinsTheFirstAndLastName() {
        when(profileQueryService.handle(new GetProfileByUserIdQuery(4L))).thenReturn(Optional.of(
                new Profile(4L, "Lucía", "Ramos", new EmailAddress("t1@x.com"))));

        assertEquals(Optional.of("Lucía Ramos"), facade.fetchFullNameByUserId(4L));
    }

    @Test
    void leavesNoTrailingSpaceWithoutLastName() {
        when(profileQueryService.handle(new GetProfileByUserIdQuery(5L))).thenReturn(Optional.of(
                new Profile(5L, "Lucía", "", new EmailAddress("t2@x.com"))));

        assertEquals(Optional.of("Lucía"), facade.fetchFullNameByUserId(5L));
    }
}