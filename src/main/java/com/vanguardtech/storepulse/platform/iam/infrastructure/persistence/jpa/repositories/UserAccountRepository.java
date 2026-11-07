package com.vanguardtech.storepulse.platform.iam.infrastructure.persistence.jpa.repositories;

import com.vanguardtech.storepulse.platform.iam.domain.model.aggregates.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/** Persistence of {@link UserAccount}. Emails are compared already normalized. */
@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    /**
     * @param address normalized sign-in email
     * @return the account that uses the email, if any
     */
    Optional<UserAccount> findByEmailAddress(String address);

    /**
     * @param address normalized sign-in email
     * @return {@code true} when an account already uses the email
     */
    boolean existsByEmailAddress(String address);

    /**
     * @param address normalized sign-in email
     * @param id      account to leave out of the search
     * @return {@code true} when another account already uses the email
     */
    boolean existsByEmailAddressAndIdNot(String address, Long id);
}
