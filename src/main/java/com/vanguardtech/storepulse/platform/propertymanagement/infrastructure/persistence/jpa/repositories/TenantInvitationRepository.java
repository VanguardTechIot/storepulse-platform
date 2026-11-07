package com.vanguardtech.storepulse.platform.propertymanagement.infrastructure.persistence.jpa.repositories;

import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.aggregates.TenantInvitation;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.valueobjects.InvitationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TenantInvitationRepository extends JpaRepository<TenantInvitation, Long> {
    List<TenantInvitation> findByGalleryIdOrderBySentAtDesc(Long galleryId);
    List<TenantInvitation> findByUnitIdAndStatus(Long unitId, InvitationStatus status);
    Optional<TenantInvitation> findByToken(String token);
}
