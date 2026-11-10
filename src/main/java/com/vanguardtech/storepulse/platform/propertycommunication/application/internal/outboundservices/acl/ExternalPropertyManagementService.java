package com.vanguardtech.storepulse.platform.propertycommunication.application.internal.outboundservices.acl;

import com.vanguardtech.storepulse.platform.propertymanagement.interfaces.acl.CommercialUnitSummary;
import com.vanguardtech.storepulse.platform.propertymanagement.interfaces.acl.PropertyManagementContextFacade;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

/**
 * Anti-corruption layer over Property Management: Communication only needs to know whether a store
 * can receive a tenant, who manages its gallery, which units and resources a gallery has and to flip
 * their occupancy.
 */
@Service("propertyCommunicationExternalPropertyManagementService")
public class ExternalPropertyManagementService {

    private final PropertyManagementContextFacade propertyManagementContextFacade;

    public ExternalPropertyManagementService(PropertyManagementContextFacade propertyManagementContextFacade) {
        this.propertyManagementContextFacade = propertyManagementContextFacade;
    }

    public Optional<CommercialUnitSummary> fetchUnit(Long unitId) {
        return propertyManagementContextFacade.fetchUnitById(unitId);
    }

    /** @return the active stores of the gallery managed by the administrator */
    public List<Long> fetchStoreIdsByAdministratorId(Long administratorId) {
        return propertyManagementContextFacade.fetchGalleryIdByAdministratorId(administratorId)
                .map(propertyManagementContextFacade::fetchActiveUnitsByGalleryId)
                .orElseGet(List::of).stream()
                .filter(CommercialUnitSummary::store)
                .map(CommercialUnitSummary::id)
                .toList();
    }

    /** @return every active unit of the gallery managed by the administrator, stores and common areas */
    public List<Long> fetchUnitIdsByAdministratorId(Long administratorId) {
        return propertyManagementContextFacade.fetchGalleryIdByAdministratorId(administratorId)
                .map(propertyManagementContextFacade::fetchActiveUnitsByGalleryId)
                .orElseGet(List::of).stream()
                .map(CommercialUnitSummary::id)
                .toList();
    }

    public List<Long> fetchResourceIdsByAdministratorId(Long administratorId) {
        return propertyManagementContextFacade.fetchGalleryIdByAdministratorId(administratorId)
                .map(propertyManagementContextFacade::fetchResourceIdsByGalleryId)
                .orElseGet(List::of);
    }

    public Optional<Long> fetchResourceIdByUnitId(Long unitId) {
        return propertyManagementContextFacade.fetchResourceIdByUnitId(unitId);
    }

    public Optional<Long> fetchAdministratorIdByGalleryId(Long galleryId) {
        return propertyManagementContextFacade.fetchAdministratorIdByGalleryId(galleryId);
    }

    /** Tells whether the gallery is managed by the administrator. */
    public boolean belongsToAdministrator(Long galleryId, Long administratorId) {
        return fetchAdministratorIdByGalleryId(galleryId).map(administratorId::equals).orElse(false);
    }

    public void markUnitOccupied(Long unitId) {
        propertyManagementContextFacade.changeUnitOccupancy(unitId, true);
    }

    public void markUnitAvailable(Long unitId) {
        propertyManagementContextFacade.changeUnitOccupancy(unitId, false);
    }
}