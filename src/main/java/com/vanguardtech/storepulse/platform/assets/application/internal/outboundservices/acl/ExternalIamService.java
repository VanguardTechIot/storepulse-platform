package com.vanguardtech.storepulse.platform.assets.application.internal.outboundservices.acl;

import com.vanguardtech.storepulse.platform.iam.interfaces.acl.IamContextFacade;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

/** Reads from Identity and Access Management which active gallery administrator sent the request. */
@Service("assetsExternalIamService")
public class ExternalIamService {

    private final IamContextFacade iamContextFacade;

    public ExternalIamService(IamContextFacade iamContextFacade) {
        this.iamContextFacade = iamContextFacade;
    }

    /** @throws AccessDeniedException when the account is not an active gallery administrator */
    public Long fetchAuthenticatedAdministratorId() {
        return iamContextFacade.fetchAuthenticatedUserId()
                .filter(iamContextFacade::isActiveGalleryAdministrator)
                .orElseThrow(() -> new AccessDeniedException("Not an active gallery administrator"));
    }
}