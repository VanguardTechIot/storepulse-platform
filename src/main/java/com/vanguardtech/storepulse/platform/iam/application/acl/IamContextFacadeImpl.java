package com.vanguardtech.storepulse.platform.iam.application.acl;

import com.vanguardtech.storepulse.platform.iam.domain.model.aggregates.UserAccount;
import com.vanguardtech.storepulse.platform.iam.domain.model.commands.ChangeUserEmailCommand;
import com.vanguardtech.storepulse.platform.iam.domain.model.queries.GetUserAccountByEmailQuery;
import com.vanguardtech.storepulse.platform.iam.domain.model.queries.GetUserAccountByIdQuery;
import com.vanguardtech.storepulse.platform.iam.domain.services.UserCommandService;
import com.vanguardtech.storepulse.platform.iam.domain.services.UserQueryService;
import com.vanguardtech.storepulse.platform.iam.infrastructure.authorization.UserPrincipal;
import com.vanguardtech.storepulse.platform.iam.interfaces.acl.IamContextFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/** Implementation of {@link IamContextFacade} on top of the IAM command and query services. */
@Service
@RequiredArgsConstructor
public class IamContextFacadeImpl implements IamContextFacade {

    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;

    @Override
    public Optional<Long> fetchAuthenticatedUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal principal)
            return Optional.of(principal.userId());
        return Optional.empty();
    }

    @Override
    public Optional<Long> fetchUserIdByEmail(String email) {
        return userQueryService.handle(new GetUserAccountByEmailQuery(email)).map(UserAccount::getId);
    }

    @Override
    public Optional<String> fetchEmailByUserId(Long userId) {
        return userQueryService.handle(new GetUserAccountByIdQuery(userId))
                .map(userAccount -> userAccount.getEmail().address());
    }

    @Override
    public boolean existsUserById(Long userId) {
        return userQueryService.handle(new GetUserAccountByIdQuery(userId)).isPresent();
    }

    @Override
    public boolean isActiveGalleryAdministrator(Long userId) {
        return userQueryService.handle(new GetUserAccountByIdQuery(userId))
                .filter(UserAccount::isActive)
                .map(UserAccount::isGalleryAdministrator)
                .orElse(false);
    }

    @Override
    public void updateUserEmail(Long userId, String email) {
        userCommandService.handle(new ChangeUserEmailCommand(userId, email));
    }
}
