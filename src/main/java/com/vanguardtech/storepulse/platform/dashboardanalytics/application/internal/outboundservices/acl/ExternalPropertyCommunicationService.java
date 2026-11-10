package com.vanguardtech.storepulse.platform.dashboardanalytics.application.internal.outboundservices.acl;

import com.vanguardtech.storepulse.platform.propertycommunication.interfaces.acl.PropertyCommunicationContextFacade;
import org.springframework.stereotype.Service;

import java.util.List;

/** Reads from Property Communication which monitored resources the authenticated user may see. */
@Service("analyticsExternalPropertyCommunicationService")
public class ExternalPropertyCommunicationService {

    private final PropertyCommunicationContextFacade propertyCommunicationContextFacade;

    public ExternalPropertyCommunicationService(PropertyCommunicationContextFacade propertyCommunicationContextFacade) {
        this.propertyCommunicationContextFacade = propertyCommunicationContextFacade;
    }

    public List<Long> fetchResourceIdsVisibleToAuthenticatedUser() {
        return propertyCommunicationContextFacade.fetchResourceIdsVisibleToAuthenticatedUser();
    }
}