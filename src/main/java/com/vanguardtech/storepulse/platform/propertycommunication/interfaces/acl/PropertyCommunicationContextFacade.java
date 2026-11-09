package com.vanguardtech.storepulse.platform.propertycommunication.interfaces.acl;

import java.util.List;
import java.util.Optional;

/**
 * Entry point of Property Communication for the other Bounded Contexts: who occupies each store
 * and a way to notify a tenant without knowing the notification model.
 */
public interface PropertyCommunicationContextFacade {

    Optional<Long> fetchActiveTenantIdByUnitId(Long commercialUnitId);

    /** @return the store the tenant occupies now, if any */
    Optional<Long> fetchActiveUnitIdByTenantId(Long tenantId);

    List<ActiveTenantAssignmentSummary> fetchActiveAssignmentsByAdministratorId(Long galleryAdministratorId);

    /**
     * Units whose data the authenticated user may see: every active unit of the administrator's
     * gallery, or the store the tenant occupies now.
     */
    List<Long> fetchUnitIdsVisibleToAuthenticatedUser();

    /** Monitored resources linked to the units visible to the authenticated user. */
    List<Long> fetchResourceIdsVisibleToAuthenticatedUser();

    /**
     * Sends an in-app notification to a tenant.
     *
     * @param type one of the {@code NotificationType} names, for example {@code EMERGENCY_ALERT}
     * @return the identifier of the notification
     */
    Long notifyTenant(Long tenantId, Long commercialUnitId, Long galleryAdministratorId, String type, String title,
                      String body, Long referenceId);
}