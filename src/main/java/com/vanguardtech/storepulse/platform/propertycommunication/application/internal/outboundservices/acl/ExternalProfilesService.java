package com.vanguardtech.storepulse.platform.propertycommunication.application.internal.outboundservices.acl;

import com.vanguardtech.storepulse.platform.profilespreferences.interfaces.acl.ProfilesContextFacade;
import org.springframework.stereotype.Service;

import java.util.Optional;

/** Reads from Profiles and Preferences the name shown for a tenant. */
@Service("propertyCommunicationExternalProfilesService")
public class ExternalProfilesService {

    private final ProfilesContextFacade profilesContextFacade;

    public ExternalProfilesService(ProfilesContextFacade profilesContextFacade) {
        this.profilesContextFacade = profilesContextFacade;
    }

    public Optional<String> fetchFullName(Long userId) {
        return profilesContextFacade.fetchFullNameByUserId(userId);
    }
}