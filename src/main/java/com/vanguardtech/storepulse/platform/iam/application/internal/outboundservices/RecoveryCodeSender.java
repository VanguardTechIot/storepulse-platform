package com.vanguardtech.storepulse.platform.iam.application.internal.outboundservices;

import com.vanguardtech.storepulse.platform.iam.domain.model.valueobjects.Email;
import com.vanguardtech.storepulse.platform.iam.domain.model.valueobjects.PasswordResetCode;

import java.time.Instant;

/** Delivers the password recovery code to the owner of an account. */
public interface RecoveryCodeSender {

    /**
     * Sends the recovery code.
     *
     * @param email     address the code is sent to
     * @param code      plain recovery code
     * @param expiresAt moment the code stops being accepted
     * @throws IllegalStateException when the message could not be delivered
     */
    void send(Email email, PasswordResetCode code, Instant expiresAt);
}
