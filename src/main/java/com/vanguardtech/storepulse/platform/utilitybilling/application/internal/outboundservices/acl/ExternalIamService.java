package com.vanguardtech.storepulse.platform.utilitybilling.application.internal.outboundservices.acl;

import com.vanguardtech.storepulse.platform.iam.interfaces.acl.IamContextFacade;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/** Reads from Identity and Access Management which user sent the request, whether it is an administrator and its email. */
@Service("utilityBillingExternalIamService")
public class ExternalIamService {

    private final IamContextFacade iamContextFacade;

    public ExternalIamService(IamContextFacade iamContextFacade) {
        this.iamContextFacade = iamContextFacade;
    }

    public Optional<Long> fetchAuthenticatedUserId() {
        return iamContextFacade.fetchAuthenticatedUserId();
    }

    /** @throws AccessDeniedException when the account is not an active gallery administrator */
    public Long fetchAuthenticatedAdministratorId() {
        return iamContextFacade.fetchAuthenticatedUserId()
                .filter(iamContextFacade::isActiveGalleryAdministrator)
                .orElseThrow(() -> new AccessDeniedException("Not an active gallery administrator"));
    }

    public Optional<String> fetchEmailByUserId(Long userId) {
        return iamContextFacade.fetchEmailByUserId(userId);
    }
}