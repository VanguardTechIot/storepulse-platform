package com.vanguardtech.storepulse.platform.propertymanagement.infrastructure.persistence.jpa.repositories;

import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.aggregates.CommercialUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommercialUnitRepository extends JpaRepository<CommercialUnit, Long> {
    List<CommercialUnit> findByGalleryIdOrderByFloorAscCodeAsc(Long galleryId);
    List<CommercialUnit> findByGalleryIdAndActiveTrue(Long galleryId);
}
