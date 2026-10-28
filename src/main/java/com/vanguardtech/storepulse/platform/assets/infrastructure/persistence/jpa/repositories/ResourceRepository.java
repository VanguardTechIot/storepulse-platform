package com.vanguardtech.storepulse.platform.assets.infrastructure.persistence.jpa.repositories;

import com.vanguardtech.storepulse.platform.assets.domain.model.aggregates.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {
}
