package com.vanguardtech.storepulse.platform.iam.domain.exceptions;

import com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException;

/**
 * The user could not be authenticated. Unlike other business rule violations it is answered with
 * {@code 401 Unauthorized}.
 */
public class AuthenticationFailedException extends BusinessRuleException {

    /**
     * Creates the exception for the given identity error.
     *
     * @param code reason the authentication failed, for example {@link IamError#INVALID_CREDENTIALS}
     */
    public AuthenticationFailedException(IamError code) {
        super(code);
    }
}
