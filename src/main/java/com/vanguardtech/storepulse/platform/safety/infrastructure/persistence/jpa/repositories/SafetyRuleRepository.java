package com.vanguardtech.storepulse.platform.safety.infrastructure.persistence.jpa.repositories;

import com.vanguardtech.storepulse.platform.safety.domain.model.aggregates.SafetyRule;
import com.vanguardtech.storepulse.platform.safety.domain.model.valueobjects.SensorType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SafetyRuleRepository extends JpaRepository<SafetyRule, Long> {
    boolean existsByName(String name);

    List<SafetyRule> findBySensorType(SensorType sensorType);
}
