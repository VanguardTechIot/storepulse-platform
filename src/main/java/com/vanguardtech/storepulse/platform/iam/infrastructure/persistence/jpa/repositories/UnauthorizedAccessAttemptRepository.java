package com.vanguardtech.storepulse.platform.iam.infrastructure.persistence.jpa.repositories;

import com.vanguardtech.storepulse.platform.iam.domain.model.aggregates.UnauthorizedAccessAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Persistence of {@link UnauthorizedAccessAttempt}. */
@Repository
public interface UnauthorizedAccessAttemptRepository extends JpaRepository<UnauthorizedAccessAttempt, Long> {
}
