package com.vanguardtech.storepulse.platform.propertycommunication.infrastructure.persistence.jpa.repositories;

import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.aggregates.TenantAssignment;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.valueobjects.AssignmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TenantAssignmentRepository extends JpaRepository<TenantAssignment, Long> {
    List<TenantAssignment> findByGalleryAdministratorIdOrderByAssignedAtDesc(Long galleryAdministratorId);
    Optional<TenantAssignment> findByCommercialUnitIdAndStatus(Long commercialUnitId, AssignmentStatus status);
    Optional<TenantAssignment> findFirstByTenantIdAndStatus(Long tenantId, AssignmentStatus status);
    List<TenantAssignment> findByGalleryAdministratorIdAndStatus(Long galleryAdministratorId, AssignmentStatus status);
}
