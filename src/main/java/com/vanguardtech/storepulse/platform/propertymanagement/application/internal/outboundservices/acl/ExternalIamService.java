package com.vanguardtech.storepulse.platform.propertymanagement.application.internal.outboundservices.acl;

import com.vanguardtech.storepulse.platform.iam.interfaces.acl.IamContextFacade;
import org.springframework.stereotype.Service;
import java.util.Optional;

/** Reads from Identity and Access Management which active gallery administrator sent the request. */
@Service("propertyManagementExternalIamService")
public class ExternalIamService {

    private final IamContextFacade iamContextFacade;

    public ExternalIamService(IamContextFacade iamContextFacade) {
        this.iamContextFacade = iamContextFacade;
    }

    public Optional<Long> fetchAuthenticatedAdministratorId() {
        return iamContextFacade.fetchAuthenticatedUserId()
                .filter(iamContextFacade::isActiveGalleryAdministrator);
    }
}
