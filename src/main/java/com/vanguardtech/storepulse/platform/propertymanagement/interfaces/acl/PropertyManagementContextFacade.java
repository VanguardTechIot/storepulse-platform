package com.vanguardtech.storepulse.platform.propertymanagement.interfaces.acl;

import java.util.List;
import java.util.Optional;

/**
 * Entry point of Property Management for the other Bounded Contexts, such as Property
 * Communication, Utility Billing, Subscriptions and Payments or Safety. It exposes only primitive
 * values and summaries.
 */
public interface PropertyManagementContextFacade {

    /** @return the gallery of the administrator, if it registered one */
    Optional<Long> fetchGalleryIdByAdministratorId(Long administratorId);

    /** @return the administrator that manages the gallery */
    Optional<Long> fetchAdministratorIdByGalleryId(Long galleryId);

    Optional<CommercialUnitSummary> fetchUnitById(Long unitId);

    /** @return every active unit of the gallery, stores and common areas */
    List<CommercialUnitSummary> fetchActiveUnitsByGalleryId(Long galleryId);

    /** @return the monitored resource of Resource and Asset Management linked to the unit, if any */
    Optional<Long> fetchResourceIdByUnitId(Long unitId);

    /** @return the active unit whose consumption is measured by the resource, if any */
    Optional<Long> fetchUnitIdByResourceId(Long resourceId);

    /** @return the gallery of the active unit linked to the resource, if any */
    Optional<Long> fetchGalleryIdByResourceId(Long resourceId);

    /** @return the monitored resources linked to the active units of the gallery */
    List<Long> fetchResourceIdsByGalleryId(Long galleryId);

    /**
     * Marks the store as occupied or available when Property Communication starts or ends a
     * tenant assignment.
     *
     * @throws com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException
     *         {@code unit_not_found}, {@code unit_not_available} or {@code common_area_without_tenant}
     */
    void changeUnitOccupancy(Long unitId, boolean occupied);
}