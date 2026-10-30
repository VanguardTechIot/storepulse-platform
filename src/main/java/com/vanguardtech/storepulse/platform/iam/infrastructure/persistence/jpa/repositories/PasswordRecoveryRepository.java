package com.vanguardtech.storepulse.platform.iam.infrastructure.persistence.jpa.repositories;

import com.vanguardtech.storepulse.platform.iam.domain.model.aggregates.PasswordRecovery;
import com.vanguardtech.storepulse.platform.iam.domain.model.valueobjects.UserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/** Persistence of {@link PasswordRecovery}. */
@Repository
public interface PasswordRecoveryRepository extends JpaRepository<PasswordRecovery, Long> {

    /**
     * Only the latest recovery of an account is valid: requesting a new code supersedes the previous one.
     *
     * @param userId account being recovered
     * @return the most recent recovery of the account, if any
     */
    Optional<PasswordRecovery> findFirstByUserIdOrderByRequestedAtDescIdDesc(UserId userId);
}
