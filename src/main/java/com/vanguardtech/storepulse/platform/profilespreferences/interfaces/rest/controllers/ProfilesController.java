package com.vanguardtech.storepulse.platform.profilespreferences.interfaces.rest.controllers;

import com.vanguardtech.storepulse.platform.profilespreferences.application.internal.outboundservices.acl.ExternalIamService;
import com.vanguardtech.storepulse.platform.profilespreferences.domain.exceptions.ProfilesError;
import com.vanguardtech.storepulse.platform.profilespreferences.domain.model.queries.GetProfileByUserIdQuery;
import com.vanguardtech.storepulse.platform.profilespreferences.domain.services.ProfileCommandService;
import com.vanguardtech.storepulse.platform.profilespreferences.domain.services.ProfileQueryService;
import com.vanguardtech.storepulse.platform.profilespreferences.interfaces.rest.resources.ProfileResource;
import com.vanguardtech.storepulse.platform.profilespreferences.interfaces.rest.resources.UpdateProfileResource;
import com.vanguardtech.storepulse.platform.profilespreferences.interfaces.rest.transform.ProfileResourceFromEntityAssembler;
import com.vanguardtech.storepulse.platform.profilespreferences.interfaces.rest.transform.UpdateProfileCommandFromResourceAssembler;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

/**
 * Profile of a user (TS-04): view it (US-06) and edit it (US-07). Each user reaches only their own
 * profile; any other answers 403 {@code access_denied}.
 */
@RestController
@RequestMapping("/users/{userId}/profile")
@RequiredArgsConstructor
@Tag(name = "Profiles")
public class ProfilesController {

    private final ProfileCommandService profileCommandService;
    private final ProfileQueryService profileQueryService;
    private final ExternalIamService externalIamService;

    /** Answers 404 {@code profile_not_found} when the account has no profile. */
    @GetMapping
    public ProfileResource getProfileByUserId(@PathVariable Long userId) {
        requireAuthenticatedOwner(userId);
        return profileQueryService.handle(new GetProfileByUserIdQuery(userId))
                .map(ProfileResourceFromEntityAssembler::toResourceFromEntity)
                .orElseThrow(() -> new ResourceNotFoundException(ProfilesError.PROFILE_NOT_FOUND));
    }

    /**
     * Replaces the profile data and photo. Answers 409 {@code invalid_name}, {@code invalid_email},
     * {@code invalid_phone_number} or {@code email_already_registered}, and 415
     * {@code unsupported_photo_format}.
     */
    @PutMapping
    public ProfileResource updateProfile(@PathVariable Long userId, @RequestBody UpdateProfileResource resource) {
        requireAuthenticatedOwner(userId);
        var command = UpdateProfileCommandFromResourceAssembler.toCommandFromResource(userId, resource);
        return ProfileResourceFromEntityAssembler.toResourceFromEntity(profileCommandService.handle(command));
    }

    /** Spring Security answers the exception with 403 {@code access_denied}. */
    private void requireAuthenticatedOwner(Long userId) {
        var authenticatedUserId = externalIamService.fetchAuthenticatedUserId();
        if (authenticatedUserId.isEmpty() || !authenticatedUserId.get().equals(userId))
            throw new AccessDeniedException("The profile belongs to another user");
    }
}
