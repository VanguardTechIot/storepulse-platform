package com.vanguardtech.storepulse.platform.iam.interfaces.rest.controllers;

import com.vanguardtech.storepulse.platform.iam.domain.model.commands.SignOutCommand;
import com.vanguardtech.storepulse.platform.iam.domain.services.UserCommandService;
import com.vanguardtech.storepulse.platform.iam.infrastructure.authorization.UserPrincipal;
import com.vanguardtech.storepulse.platform.iam.interfaces.rest.resources.AuthenticatedUserResource;
import com.vanguardtech.storepulse.platform.iam.interfaces.rest.resources.SignInResource;
import com.vanguardtech.storepulse.platform.iam.interfaces.rest.resources.SignUpResource;
import com.vanguardtech.storepulse.platform.iam.interfaces.rest.transform.AuthenticatedUserResourceFromEntityAssembler;
import com.vanguardtech.storepulse.platform.iam.interfaces.rest.transform.SignInCommandFromResourceAssembler;
import com.vanguardtech.storepulse.platform.iam.interfaces.rest.transform.SignUpCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/** Registration, sign-in and sign-out of users (US-01, US-02). */
@RestController
@RequestMapping("/authentication")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthenticationController {

    private final UserCommandService userCommandService;

    /**
     * Registers a gallery administrator and starts the session; answers 409 {@code weak_password} or
     * {@code email_already_registered}.
     */
    @PostMapping("/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    @SecurityRequirements
    public AuthenticatedUserResource signUp(@Valid @RequestBody SignUpResource resource) {
        var command = SignUpCommandFromResourceAssembler.toCommandFromResource(resource);
        return AuthenticatedUserResourceFromEntityAssembler.toResourceFromEntity(userCommandService.handle(command));
    }

    /** Issues an access token; answers 401 {@code invalid_credentials} or {@code account_inactive}. */
    @PostMapping("/sign-in")
    @SecurityRequirements
    public AuthenticatedUserResource signIn(@Valid @RequestBody SignInResource resource) {
        var command = SignInCommandFromResourceAssembler.toCommandFromResource(resource);
        return AuthenticatedUserResourceFromEntityAssembler.toResourceFromEntity(userCommandService.handle(command));
    }

    /** Ends the session of the authenticated user; the client discards its token. */
    @PostMapping("/sign-out")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void signOut(@AuthenticationPrincipal UserPrincipal principal) {
        userCommandService.handle(new SignOutCommand(principal.userId()));
    }
}
