package com.vanguardtech.storepulse.platform.iam.application.internal.outboundservices;

import com.vanguardtech.storepulse.platform.iam.domain.model.aggregates.UserAccount;

import java.util.Optional;

/** Issues and verifies the access tokens that authorize the requests of a user. */
public interface TokenService {

    /**
     * Issues an access token for an account.
     *
     * @param userAccount persisted account the token identifies
     * @return the signed token
     */
    String generateToken(UserAccount userAccount);

    /**
     * Verifies a token and reads the account it identifies.
     *
     * @param token token received in the {@code Authorization} header
     * @return the account identifier, or empty when the token is malformed, tampered or expired
     */
    Optional<Long> getUserIdFromToken(String token);
}
