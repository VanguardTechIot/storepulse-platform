package com.vanguardtech.storepulse.platform.propertymanagement.infrastructure.persistence.jpa.repositories;

import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.aggregates.CommercialUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CommercialUnitRepository extends JpaRepository<CommercialUnit, Long> {
    List<CommercialUnit> findByGalleryIdOrderByFloorAscCodeAsc(Long galleryId);
    List<CommercialUnit> findByGalleryIdAndActiveTrue(Long galleryId);

    Optional<CommercialUnit> findFirstByResourceIdAndActiveTrue(Long resourceId);

    /** The unit, only when it belongs to the gallery of the administrator. */
    @Query("select u from CommercialUnit u where u.id = :id and u.galleryId in "
            + "(select g.id from CommercialGallery g where g.administratorId = :administratorId)")
    Optional<CommercialUnit> findByIdAndAdministratorId(Long id, Long administratorId);
}