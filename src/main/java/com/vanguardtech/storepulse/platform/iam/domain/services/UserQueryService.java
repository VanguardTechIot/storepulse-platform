package com.vanguardtech.storepulse.platform.iam.domain.services;

import com.vanguardtech.storepulse.platform.iam.domain.model.aggregates.UserAccount;
import com.vanguardtech.storepulse.platform.iam.domain.model.queries.GetUserAccountByEmailQuery;
import com.vanguardtech.storepulse.platform.iam.domain.model.queries.GetUserAccountByIdQuery;

import java.util.Optional;

/** Use cases that read user accounts. */
public interface UserQueryService {

    /**
     * @param query identifier of the account
     * @return the account, or empty when it does not exist
     */
    Optional<UserAccount> handle(GetUserAccountByIdQuery query);

    /**
     * @param query sign-in email of the account
     * @return the account, or empty when no account uses that email
     */
    Optional<UserAccount> handle(GetUserAccountByEmailQuery query);
}
