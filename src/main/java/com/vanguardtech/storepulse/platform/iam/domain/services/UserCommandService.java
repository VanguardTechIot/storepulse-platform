package com.vanguardtech.storepulse.platform.iam.domain.services;

import com.vanguardtech.storepulse.platform.iam.domain.model.aggregates.UserAccount;
import com.vanguardtech.storepulse.platform.iam.domain.model.commands.ChangeUserEmailCommand;
import com.vanguardtech.storepulse.platform.iam.domain.model.commands.SignInCommand;
import com.vanguardtech.storepulse.platform.iam.domain.model.commands.SignOutCommand;
import com.vanguardtech.storepulse.platform.iam.domain.model.commands.SignUpCommand;
import com.vanguardtech.storepulse.platform.iam.domain.model.valueobjects.UserSession;

/** Use cases that change user accounts. */
public interface UserCommandService {

    /**
     * Registers a gallery administrator and starts the session.
     *
     * @param command registration data
     * @return the new account with its access token
     * @throws com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException
     *         {@code weak_password} or {@code email_already_registered}
     */
    UserSession handle(SignUpCommand command);

    /**
     * Verifies the credentials and issues an access token. Every rejection is recorded as an
     * {@code UnauthorizedAccessAttempt}.
     *
     * @param command credentials
     * @return the authenticated account with its access token
     * @throws com.vanguardtech.storepulse.platform.iam.domain.exceptions.AuthenticationFailedException
     *         {@code invalid_credentials} or {@code account_inactive}
     */
    UserSession handle(SignInCommand command);

    /**
     * Ends the session of the user.
     *
     * @param command account that signs out
     * @throws com.vanguardtech.storepulse.platform.shared.domain.exceptions.ResourceNotFoundException
     *         {@code user_not_found}
     */
    void handle(SignOutCommand command);

    /**
     * Replaces the sign-in email of an account.
     *
     * @param command account and new email
     * @return the updated account
     * @throws com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException
     *         {@code user_not_found} or {@code email_already_registered}
     */
    UserAccount handle(ChangeUserEmailCommand command);
}
