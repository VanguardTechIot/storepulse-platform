package com.vanguardtech.storepulse.platform.propertycommunication.application.internal.queryservices;

import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.aggregates.TenantAssignment;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.queries.GetActiveAssignmentByTenantIdQuery;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.queries.GetActiveAssignmentByUnitIdQuery;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.queries.GetAssignmentByIdQuery;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.queries.GetAssignmentsByAdministratorIdQuery;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.valueobjects.AssignmentStatus;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.services.TenantAssignmentQueryService;
import com.vanguardtech.storepulse.platform.propertycommunication.infrastructure.persistence.jpa.repositories.TenantAssignmentRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class TenantAssignmentQueryServiceImpl implements TenantAssignmentQueryService {

    private final TenantAssignmentRepository assignmentRepository;

    public TenantAssignmentQueryServiceImpl(TenantAssignmentRepository assignmentRepository) {
        this.assignmentRepository = assignmentRepository;
    }

    @Override
    public List<TenantAssignment> handle(GetAssignmentsByAdministratorIdQuery query) {
        return assignmentRepository.findByGalleryAdministratorIdOrderByAssignedAtDesc(query.galleryAdministratorId());
    }

    @Override
    public Optional<TenantAssignment> handle(GetAssignmentByIdQuery query) {
        return assignmentRepository.findById(query.assignmentId());
    }

    @Override
    public Optional<TenantAssignment> handle(GetActiveAssignmentByUnitIdQuery query) {
        return assignmentRepository.findByCommercialUnitIdAndStatus(query.commercialUnitId(), AssignmentStatus.ACTIVE);
    }

    @Override
    public Optional<TenantAssignment> handle(GetActiveAssignmentByTenantIdQuery query) {
        return assignmentRepository.findFirstByTenantIdAndStatus(query.tenantId(), AssignmentStatus.ACTIVE);
    }
}
