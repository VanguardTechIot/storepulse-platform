package com.vanguardtech.storepulse.platform.profilespreferences.application.internal.eventhandlers;

import com.vanguardtech.storepulse.platform.iam.domain.model.events.UserRegisteredEvent;
import com.vanguardtech.storepulse.platform.profilespreferences.domain.model.commands.CreateProfileCommand;
import com.vanguardtech.storepulse.platform.profilespreferences.domain.services.ProfileCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Creates the profile when Identity and Access Management registers an account (US-01).
 *
 * <p>It runs synchronously inside the registration transaction: if the profile cannot be created,
 * the account is not created either, so there is never an account without a profile.
 */
@Service
@RequiredArgsConstructor
public class UserRegisteredEventHandler {

    private final ProfileCommandService profileCommandService;

    /**
     * @param event the registration published by the new {@code UserAccount}
     */
    @EventListener
    public void on(UserRegisteredEvent event) {
        profileCommandService.handle(CreateProfileCommand.fromFullName(event.userId(), event.fullName(),
                event.email()));
    }
}
