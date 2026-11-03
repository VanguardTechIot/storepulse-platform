package com.vanguardtech.storepulse.platform.profilespreferences.application.internal.commandservices;

import com.vanguardtech.storepulse.platform.profilespreferences.application.internal.outboundservices.acl.ExternalIamService;
import com.vanguardtech.storepulse.platform.profilespreferences.domain.exceptions.ProfilesError;
import com.vanguardtech.storepulse.platform.profilespreferences.domain.model.aggregates.Profile;
import com.vanguardtech.storepulse.platform.profilespreferences.domain.model.commands.CreateProfileCommand;
import com.vanguardtech.storepulse.platform.profilespreferences.domain.model.commands.UpdateProfileCommand;
import com.vanguardtech.storepulse.platform.profilespreferences.domain.model.valueobjects.EmailAddress;
import com.vanguardtech.storepulse.platform.profilespreferences.domain.model.valueobjects.PhoneNumber;
import com.vanguardtech.storepulse.platform.profilespreferences.domain.model.valueobjects.ProfilePhoto;
import com.vanguardtech.storepulse.platform.profilespreferences.domain.services.ProfileCommandService;
import com.vanguardtech.storepulse.platform.profilespreferences.infrastructure.persistence.jpa.repositories.ProfileRepository;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Implementation of the use cases that change profiles. */
@Service
@RequiredArgsConstructor
public class ProfileCommandServiceImpl implements ProfileCommandService {

    private final ProfileRepository profileRepository;
    private final ExternalIamService externalIamService;

    @Override
    @Transactional
    public Profile handle(CreateProfileCommand command) {
        if (profileRepository.existsByUserId(command.userId()))
            throw new BusinessRuleException(ProfilesError.PROFILE_ALREADY_EXISTS);
        return profileRepository.save(new Profile(command.userId(), command.firstName(), command.lastName(),
                new EmailAddress(command.email())));
    }

    /**
     * All the values are built before the profile changes, so an invalid one leaves it untouched.
     * When the account rejects the new email, the whole transaction is rolled back.
     */
    @Override
    @Transactional
    public Profile handle(UpdateProfileCommand command) {
        var profile = profileRepository.findByUserId(command.userId())
                .orElseThrow(() -> new ResourceNotFoundException(ProfilesError.PROFILE_NOT_FOUND));
        var email = new EmailAddress(command.email());
        var phoneNumber = PhoneNumber.ofNullable(command.phoneNumber());
        var photo = ProfilePhoto.ofNullable(command.photoUrl());
        var emailChanged = !profile.getEmail().equals(email);

        profile.update(command.firstName(), command.lastName(), email, phoneNumber, photo);
        if (emailChanged) externalIamService.updateUserEmail(profile.getUserId(), email.value());
        return profileRepository.save(profile);
    }
}
