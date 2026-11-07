package com.vanguardtech.storepulse.platform.propertycommunication.application.internal.outboundservices.acl;

import com.vanguardtech.storepulse.platform.iam.interfaces.acl.IamContextFacade;
import org.springframework.stereotype.Service;
import java.util.Optional;

/** Reads from Identity and Access Management who sends the request and whether a tenant exists. */
@Service("propertyCommunicationExternalIamService")
public class ExternalIamService {

    private final IamContextFacade iamContextFacade;

    public ExternalIamService(IamContextFacade iamContextFacade) {
        this.iamContextFacade = iamContextFacade;
    }

    public Optional<Long> fetchAuthenticatedUserId() {
        return iamContextFacade.fetchAuthenticatedUserId();
    }

    public Optional<Long> fetchAuthenticatedAdministratorId() {
        return iamContextFacade.fetchAuthenticatedUserId().filter(iamContextFacade::isActiveGalleryAdministrator);
    }

    public boolean existsUser(Long userId) {
        return iamContextFacade.existsUserById(userId);
    }
}
