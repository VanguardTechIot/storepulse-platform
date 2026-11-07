package com.vanguardtech.storepulse.platform.iam.domain.services;

import com.vanguardtech.storepulse.platform.iam.domain.model.commands.RequestPasswordResetCommand;
import com.vanguardtech.storepulse.platform.iam.domain.model.commands.ResetPasswordCommand;

import java.time.Instant;

/** Use cases of the password recovery (US-03). */
public interface PasswordRecoveryCommandService {

    /**
     * Generates a recovery code and sends it to the email of the account. The answer is the same
     * whether or not the email is registered, so the endpoint cannot be used to discover accounts.
     *
     * @param command email of the account
     * @return the moment the code expires
     */
    Instant handle(RequestPasswordResetCommand command);

    /**
     * Validates the recovery code and replaces the password.
     *
     * @param command email, recovery code and new password
     * @throws com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException
     *         {@code weak_password}, {@code invalid_reset_code} or {@code expired_reset_code}
     */
    void handle(ResetPasswordCommand command);
}
