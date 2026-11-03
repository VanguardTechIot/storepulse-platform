package com.vanguardtech.storepulse.platform.profilespreferences.infrastructure.persistence.jpa.repositories;

import com.vanguardtech.storepulse.platform.profilespreferences.domain.model.aggregates.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/** Persistence of {@link Profile}. */
@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    /**
     * @param userId account that owns the profile
     * @return the profile of the account, if any
     */
    Optional<Profile> findByUserId(Long userId);

    /**
     * @param userId account that owns the profile
     * @return {@code true} when the account already has a profile
     */
    boolean existsByUserId(Long userId);
}
