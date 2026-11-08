package com.vanguardtech.storepulse.platform.propertymanagement.application.internal.outboundservices.acl;

import com.vanguardtech.storepulse.platform.iam.interfaces.acl.IamContextFacade;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.exceptions.PropertyManagementError;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import java.util.Optional;

/** Reads from Identity and Access Management who sent the request and the email of an account. */
@Service("propertyManagementExternalIamService")
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
                .orElseThrow(() -> new BusinessRuleException(PropertyManagementError.ADMINISTRATOR_NOT_AUTHENTICATED));
    }

    public Optional<String> fetchEmailByUserId(Long userId) {
        return iamContextFacade.fetchEmailByUserId(userId);
    }
}