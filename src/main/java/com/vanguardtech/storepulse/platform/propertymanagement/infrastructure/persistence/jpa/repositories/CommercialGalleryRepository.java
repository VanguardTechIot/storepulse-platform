package com.vanguardtech.storepulse.platform.propertymanagement.infrastructure.persistence.jpa.repositories;

import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.aggregates.CommercialGallery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CommercialGalleryRepository extends JpaRepository<CommercialGallery, Long> {
    Optional<CommercialGallery> findByAdministratorId(Long administratorId);
    boolean existsByAdministratorId(Long administratorId);
    Optional<CommercialGallery> findByIdAndAdministratorId(Long id, Long administratorId);
    boolean existsByIdAndAdministratorId(Long id, Long administratorId);
}