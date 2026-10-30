package com.vanguardtech.storepulse.platform.iam.interfaces.rest.controllers;

import com.vanguardtech.storepulse.platform.iam.domain.services.PasswordRecoveryCommandService;
import com.vanguardtech.storepulse.platform.iam.interfaces.rest.resources.PasswordResetRequestedResource;
import com.vanguardtech.storepulse.platform.iam.interfaces.rest.resources.RequestPasswordResetResource;
import com.vanguardtech.storepulse.platform.iam.interfaces.rest.resources.ResetPasswordResource;
import com.vanguardtech.storepulse.platform.iam.interfaces.rest.transform.RequestPasswordResetCommandFromResourceAssembler;
import com.vanguardtech.storepulse.platform.iam.interfaces.rest.transform.ResetPasswordCommandFromResourceAssembler;
import com.vanguardtech.storepulse.platform.shared.interfaces.rest.resources.MessageResource;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/** Recovery of a forgotten password with a 6-digit code sent by email (US-03). */
@RestController
@RequestMapping("/password-recoveries")
@RequiredArgsConstructor
@Tag(name = "Password Recoveries")
@SecurityRequirements
public class PasswordRecoveryController {

    private final PasswordRecoveryCommandService passwordRecoveryCommandService;

    /**
     * Sends a recovery code to the email. The answer is the same whether or not the email is
     * registered, so it cannot be used to discover accounts.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public PasswordResetRequestedResource requestPasswordReset(@Valid @RequestBody RequestPasswordResetResource resource) {
        var command = RequestPasswordResetCommandFromResourceAssembler.toCommandFromResource(resource);
        return new PasswordResetRequestedResource(passwordRecoveryCommandService.handle(command));
    }

    /**
     * Replaces the password; answers 409 {@code weak_password}, {@code invalid_reset_code} or
     * {@code expired_reset_code}.
     */
    @PostMapping("/reset")
    public MessageResource resetPassword(@Valid @RequestBody ResetPasswordResource resource) {
        passwordRecoveryCommandService.handle(ResetPasswordCommandFromResourceAssembler.toCommandFromResource(resource));
        return new MessageResource("password_reset");
    }
}
