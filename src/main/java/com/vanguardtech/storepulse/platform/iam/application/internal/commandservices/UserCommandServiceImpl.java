package com.vanguardtech.storepulse.platform.iam.application.internal.commandservices;

import com.vanguardtech.storepulse.platform.iam.application.internal.outboundservices.PasswordHasher;
import com.vanguardtech.storepulse.platform.iam.application.internal.outboundservices.TokenService;
import com.vanguardtech.storepulse.platform.iam.domain.exceptions.AuthenticationFailedException;
import com.vanguardtech.storepulse.platform.iam.domain.exceptions.IamError;
import com.vanguardtech.storepulse.platform.iam.domain.model.aggregates.UnauthorizedAccessAttempt;
import com.vanguardtech.storepulse.platform.iam.domain.model.aggregates.UserAccount;
import com.vanguardtech.storepulse.platform.iam.domain.model.commands.ChangeUserEmailCommand;
import com.vanguardtech.storepulse.platform.iam.domain.model.commands.SignInCommand;
import com.vanguardtech.storepulse.platform.iam.domain.model.commands.SignOutCommand;
import com.vanguardtech.storepulse.platform.iam.domain.model.commands.SignUpCommand;
import com.vanguardtech.storepulse.platform.iam.domain.model.valueobjects.Email;
import com.vanguardtech.storepulse.platform.iam.domain.model.valueobjects.PasswordHash;
import com.vanguardtech.storepulse.platform.iam.domain.model.valueobjects.PasswordPolicy;
import com.vanguardtech.storepulse.platform.iam.domain.model.valueobjects.Role;
import com.vanguardtech.storepulse.platform.iam.domain.model.valueobjects.UserSession;
import com.vanguardtech.storepulse.platform.iam.domain.services.UserCommandService;
import com.vanguardtech.storepulse.platform.iam.infrastructure.persistence.jpa.repositories.UnauthorizedAccessAttemptRepository;
import com.vanguardtech.storepulse.platform.iam.infrastructure.persistence.jpa.repositories.UserAccountRepository;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/** Implementation of the use cases that change user accounts. */
@Service
@RequiredArgsConstructor
public class UserCommandServiceImpl implements UserCommandService {

    private final UserAccountRepository userAccountRepository;
    private final UnauthorizedAccessAttemptRepository unauthorizedAccessAttemptRepository;
    private final PasswordHasher passwordHasher;
    private final TokenService tokenService;

    /**
     * Only gallery administrators register themselves (US-01); the account is saved first so the
     * registration event carries its identifier.
     */
    @Override
    @Transactional
    public UserSession handle(SignUpCommand command) {
        if (!PasswordPolicy.isSatisfiedBy(command.password())) throw new BusinessRuleException(IamError.WEAK_PASSWORD);
        var email = new Email(command.email());
        if (userAccountRepository.existsByEmailAddress(email.address()))
            throw new BusinessRuleException(IamError.EMAIL_ALREADY_REGISTERED);

        var passwordHash = new PasswordHash(passwordHasher.encode(command.password()));
        var userAccount = userAccountRepository.save(new UserAccount(email, passwordHash, Role.GALLERY_ADMINISTRATOR));
        userAccount.confirmRegistration(command.fullName().trim(), Instant.now());
        userAccount = userAccountRepository.save(userAccount);
        return new UserSession(userAccount, tokenService.generateToken(userAccount));
    }

    /**
     * The status is checked only after the password matches, so a wrong password never reveals
     * whether an account is disabled. Rejections are kept even though the exception is thrown.
     */
    @Override
    @Transactional(noRollbackFor = AuthenticationFailedException.class)
    public UserSession handle(SignInCommand command) {
        var email = Email.normalize(command.email());
        var now = Instant.now();
        var userAccount = userAccountRepository.findByEmailAddress(email)
                .filter(account -> passwordHasher.matches(command.password(), account.getPasswordHash().value()))
                .orElseThrow(() -> reject(email, null, IamError.INVALID_CREDENTIALS, now));
        if (!userAccount.isActive()) throw reject(email, userAccount.getId(), IamError.ACCOUNT_INACTIVE, now);

        userAccount.signIn(now);
        userAccount = userAccountRepository.save(userAccount);
        return new UserSession(userAccount, tokenService.generateToken(userAccount));
    }

    @Override
    public void handle(SignOutCommand command) {
        var userAccount = findUserAccount(command.userId());
        userAccount.signOut(Instant.now());
        userAccountRepository.save(userAccount);
    }

    @Override
    @Transactional
    public UserAccount handle(ChangeUserEmailCommand command) {
        var userAccount = findUserAccount(command.userId());
        var email = new Email(command.email());
        if (userAccountRepository.existsByEmailAddressAndIdNot(email.address(), userAccount.getId()))
            throw new BusinessRuleException(IamError.EMAIL_ALREADY_REGISTERED);
        userAccount.changeEmail(email);
        return userAccountRepository.save(userAccount);
    }

    private UserAccount findUserAccount(Long userId) {
        return userAccountRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(IamError.USER_NOT_FOUND));
    }

    /** Records the rejected sign-in and builds the exception the caller throws. */
    private AuthenticationFailedException reject(String email, Long userId, IamError reason, Instant at) {
        unauthorizedAccessAttemptRepository.save(new UnauthorizedAccessAttempt(email, userId, reason, at));
        return new AuthenticationFailedException(reason);
    }
}
