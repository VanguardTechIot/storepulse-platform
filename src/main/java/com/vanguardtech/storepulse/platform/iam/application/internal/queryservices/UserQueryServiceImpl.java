package com.vanguardtech.storepulse.platform.iam.application.internal.queryservices;

import com.vanguardtech.storepulse.platform.iam.domain.model.aggregates.UserAccount;
import com.vanguardtech.storepulse.platform.iam.domain.model.queries.GetUserAccountByEmailQuery;
import com.vanguardtech.storepulse.platform.iam.domain.model.queries.GetUserAccountByIdQuery;
import com.vanguardtech.storepulse.platform.iam.domain.model.valueobjects.Email;
import com.vanguardtech.storepulse.platform.iam.domain.services.UserQueryService;
import com.vanguardtech.storepulse.platform.iam.infrastructure.persistence.jpa.repositories.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/** Implementation of the use cases that read user accounts. */
@Service
@RequiredArgsConstructor
public class UserQueryServiceImpl implements UserQueryService {

    private final UserAccountRepository userAccountRepository;

    @Override
    public Optional<UserAccount> handle(GetUserAccountByIdQuery query) {
        return userAccountRepository.findById(query.userId());
    }

    @Override
    public Optional<UserAccount> handle(GetUserAccountByEmailQuery query) {
        return userAccountRepository.findByEmailAddress(Email.normalize(query.email()));
    }
}
