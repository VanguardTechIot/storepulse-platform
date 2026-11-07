package com.vanguardtech.storepulse.platform.subscriptionspayments.application.internal.outboundservices.acl;

import com.vanguardtech.storepulse.platform.iam.interfaces.acl.IamContextFacade;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.valueobjects.AdministratorId;
import org.springframework.stereotype.Service;

import java.util.Optional;

/** Reads from Identity and Access Management which active gallery administrator sent the request. */
@Service("subscriptionsExternalIamService")
public class ExternalIamService {

    private final IamContextFacade iamContextFacade;

    public ExternalIamService(IamContextFacade iamContextFacade) {
        this.iamContextFacade = iamContextFacade;
    }

    public Optional<AdministratorId> fetchAuthenticatedAdministratorId() {
        return iamContextFacade.fetchAuthenticatedUserId()
                .filter(iamContextFacade::isActiveGalleryAdministrator)
                .map(AdministratorId::new);
    }
}