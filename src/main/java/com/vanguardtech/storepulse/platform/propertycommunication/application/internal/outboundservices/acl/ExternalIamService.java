package com.vanguardtech.storepulse.platform.propertycommunication.application.internal.outboundservices.acl;

import com.vanguardtech.storepulse.platform.propertycommunication.domain.exceptions.PropertyCommunicationError;
import com.vanguardtech.storepulse.platform.iam.interfaces.acl.IamContextFacade;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/** Reads from Identity and Access Management who sends the request and whether a tenant exists. */
@Service("propertyCommunicationExternalIamService")
public class ExternalIamService {

    private final IamContextFacade iamContextFacade;

    public ExternalIamService(IamContextFacade iamContextFacade) {
        this.iamContextFacade = iamContextFacade;
    }

    public Long fetchAuthenticatedUserId() {
        return iamContextFacade.fetchAuthenticatedUserId().orElseThrow(() -> new AccessDeniedException("No user"));
    }

    /** @throws BusinessRuleException {@code administrator_not_authenticated} when the account is not an active administrator */
    public Long fetchAuthenticatedAdministratorId() {
        return iamContextFacade.fetchAuthenticatedUserId()
                .filter(iamContextFacade::isActiveGalleryAdministrator)
                .orElseThrow(() -> new BusinessRuleException(PropertyCommunicationError.ADMINISTRATOR_NOT_AUTHENTICATED));
    }

    public boolean isGalleryAdministrator(Long userId) {
        return iamContextFacade.isActiveGalleryAdministrator(userId);
    }

    public boolean existsUser(Long userId) {
        return iamContextFacade.existsUserById(userId);
    }

    public Optional<String> fetchEmail(Long userId) {
        return iamContextFacade.fetchEmailByUserId(userId);
    }
}