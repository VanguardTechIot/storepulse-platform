package com.vanguardtech.storepulse.platform.profilespreferences.application.internal.outboundservices.acl;

import com.vanguardtech.storepulse.platform.iam.interfaces.acl.IamContextFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Reads from and writes to Identity and Access Management what a profile needs: who is
 * authenticated and the sign-in email, which is also the profile email.
 */
@Service("profilesExternalIamService")
@RequiredArgsConstructor
public class ExternalIamService {

    private final IamContextFacade iamContextFacade;

    /** @return the identifier of the authenticated user, or empty when the request is anonymous */
    public Optional<Long> fetchAuthenticatedUserId() {
        return iamContextFacade.fetchAuthenticatedUserId();
    }

    /**
     * Keeps the sign-in email equal to the profile email.
     *
     * @param userId identifier of the account
     * @param email  new email of the profile
     * @throws com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException
     *         {@code email_already_registered} when another account uses the email
     */
    public void updateUserEmail(Long userId, String email) {
        iamContextFacade.updateUserEmail(userId, email);
    }
}
