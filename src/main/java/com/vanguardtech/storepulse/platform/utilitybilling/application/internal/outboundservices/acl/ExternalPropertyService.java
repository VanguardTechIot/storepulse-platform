package com.vanguardtech.storepulse.platform.utilitybilling.application.internal.outboundservices.acl;

import com.vanguardtech.storepulse.platform.propertycommunication.interfaces.acl.PropertyCommunicationContextFacade;
import com.vanguardtech.storepulse.platform.propertymanagement.interfaces.acl.CommercialUnitSummary;
import com.vanguardtech.storepulse.platform.propertymanagement.interfaces.acl.PropertyManagementContextFacade;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Anti-corruption layer over Property Management and Property Communication: which store is billed,
 * who manages it, who occupies it and which resource measures its consumption.
 */
@Service("utilityBillingExternalPropertyService")
public class ExternalPropertyService {

    private final PropertyManagementContextFacade propertyManagementContextFacade;
    private final PropertyCommunicationContextFacade propertyCommunicationContextFacade;

    public ExternalPropertyService(PropertyManagementContextFacade propertyManagementContextFacade,
                                   PropertyCommunicationContextFacade propertyCommunicationContextFacade) {
        this.propertyManagementContextFacade = propertyManagementContextFacade;
        this.propertyCommunicationContextFacade = propertyCommunicationContextFacade;
    }

    /** The unit, only when it belongs to the gallery of the administrator. */
    public Optional<CommercialUnitSummary> fetchUnitOfAdministrator(Long unitId, Long administratorId) {
        var galleryId = propertyManagementContextFacade.fetchGalleryIdByAdministratorId(administratorId);
        return propertyManagementContextFacade.fetchUnitById(unitId)
                .filter(unit -> galleryId.map(unit.galleryId()::equals).orElse(false));
    }

    public boolean isUnitOfAdministrator(Long unitId, Long administratorId) {
        return fetchUnitOfAdministrator(unitId, administratorId).isPresent();
    }

    public Optional<Long> fetchResourceIdByUnitId(Long unitId) {
        return propertyManagementContextFacade.fetchResourceIdByUnitId(unitId);
    }

    public Optional<Long> fetchActiveTenantIdByUnitId(Long unitId) {
        return propertyCommunicationContextFacade.fetchActiveTenantIdByUnitId(unitId);
    }

    /** Tells whether the tenant occupies the unit now. */
    public boolean isUnitOfTenant(Long unitId, Long tenantId) {
        return propertyCommunicationContextFacade.fetchActiveUnitIdByTenantId(tenantId).map(unitId::equals).orElse(false);
    }

    /** Units whose bills the authenticated user may see. */
    public List<Long> fetchUnitIdsVisibleToAuthenticatedUser() {
        return propertyCommunicationContextFacade.fetchUnitIdsVisibleToAuthenticatedUser();
    }
}