package com.vanguardtech.storepulse.platform.monitoring.application.internal.outboundservices.acl;

import com.vanguardtech.storepulse.platform.propertycommunication.interfaces.acl.PropertyCommunicationContextFacade;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("monitoringExternalPropertyCommunicationService")
public class ExternalPropertyCommunicationService {

    private final PropertyCommunicationContextFacade propertyCommunicationContextFacade;

    public ExternalPropertyCommunicationService(PropertyCommunicationContextFacade propertyCommunicationContextFacade) {
        this.propertyCommunicationContextFacade = propertyCommunicationContextFacade;
    }

    public List<Long> fetchResourceIdsVisibleToAuthenticatedUser() {
        return propertyCommunicationContextFacade.fetchResourceIdsVisibleToAuthenticatedUser();
    }
}