package com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.transform;

import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.aggregates.TenantAssignment;
import com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.resources.TenantAssignmentResource;

public class TenantAssignmentResourceFromEntityAssembler {
    public static TenantAssignmentResource toResourceFromEntity(TenantAssignment assignment) {
        return new TenantAssignmentResource(assignment.getId(), assignment.getTenantId(), assignment.getCommercialUnitId(),
                assignment.getGalleryAdministratorId(), assignment.getStatus().name(), assignment.getAssignedAt(),
                assignment.getTerminatedAt());
    }
}
