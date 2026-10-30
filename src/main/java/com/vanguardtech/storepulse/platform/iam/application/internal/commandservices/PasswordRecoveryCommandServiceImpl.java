package com.vanguardtech.storepulse.platform.iam.application.internal.commandservices;

import com.vanguardtech.storepulse.platform.iam.application.internal.outboundservices.PasswordHasher;
import com.vanguardtech.storepulse.platform.iam.application.internal.outboundservices.RecoveryCodeSender;
import com.vanguardtech.storepulse.platform.iam.domain.exceptions.IamError;
import com.vanguardtech.storepulse.platform.iam.domain.model.aggregates.PasswordRecovery;
import com.vanguardtech.storepulse.platform.iam.domain.model.commands.RequestPasswordResetCommand;
import com.vanguardtech.storepulse.platform.iam.domain.model.commands.ResetPasswordCommand;
import com.vanguardtech.storepulse.platform.iam.domain.model.valueobjects.Email;
import com.vanguardtech.storepulse.platform.iam.domain.model.valueobjects.PasswordHash;
import com.vanguardtech.storepulse.platform.iam.domain.model.valueobjects.PasswordPolicy;
import com.vanguardtech.storepulse.platform.iam.domain.model.valueobjects.PasswordResetCode;
import com.vanguardtech.storepulse.platform.iam.domain.services.PasswordRecoveryCommandService;
import com.vanguardtech.storepulse.platform.iam.infrastructure.persistence.jpa.repositories.PasswordRecoveryRepository;
import com.vanguardtech.storepulse.platform.iam.infrastructure.persistence.jpa.repositories.UserAccountRepository;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/** Implementation of the password recovery use cases (US-03). */
@Service
@RequiredArgsConstructor
public class PasswordRecoveryCommandServiceImpl implements PasswordRecoveryCommandService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordRecoveryRepository passwordRecoveryRepository;
    private final PasswordHasher passwordHasher;
    private final RecoveryCodeSender recoveryCodeSender;

    /**
     * The code is delivered inside the transaction: when it cannot be sent, the recovery is not
     * kept and the error reaches the caller.
     */
    @Override
    @Transactional
    public Instant handle(RequestPasswordResetCommand command) {
        var now = Instant.now();
        var email = new Email(command.email());
        var userAccount = userAccountRepository.findByEmailAddress(email.address());
        if (userAccount.isEmpty()) return now.plus(PasswordRecovery.VALIDITY);

        var account = userAccount.get();
        var code = PasswordResetCode.generate();
        var passwordRecovery = passwordRecoveryRepository.save(
                new PasswordRecovery(account.getUserId(), passwordHasher.encode(code.value()), now));
        account.requestPasswordReset(now);
        userAccountRepository.save(account);
        recoveryCodeSender.send(account.getEmail(), code, passwordRecovery.getExpiresAt());
        return passwordRecovery.getExpiresAt();
    }

    /**
     * Only the latest recovery of the account is checked. A wrong code counts towards the limit of
     * failed attempts, which is kept even though the exception is thrown.
     */
    @Override
    @Transactional(noRollbackFor = BusinessRuleException.class)
    public void handle(ResetPasswordCommand command) {
        if (!PasswordPolicy.isSatisfiedBy(command.newPassword())) throw new BusinessRuleException(IamError.WEAK_PASSWORD);
        var userAccount = userAccountRepository.findByEmailAddress(Email.normalize(command.email()))
                .orElseThrow(() -> new BusinessRuleException(IamError.INVALID_RESET_CODE));
        var passwordRecovery = passwordRecoveryRepository
                .findFirstByUserIdOrderByRequestedAtDescIdDesc(userAccount.getUserId())
                .filter(PasswordRecovery::isOpen)
                .orElseThrow(() -> new BusinessRuleException(IamError.INVALID_RESET_CODE));

        var code = command.code() == null ? "" : command.code().trim();
        if (!PasswordResetCode.isValid(code) || !passwordHasher.matches(code, passwordRecovery.getCodeHash())) {
            passwordRecovery.registerFailedAttempt();
            passwordRecoveryRepository.save(passwordRecovery);
            throw new BusinessRuleException(IamError.INVALID_RESET_CODE);
        }
        var now = Instant.now();
        if (passwordRecovery.isExpired(now)) throw new BusinessRuleException(IamError.EXPIRED_RESET_CODE);

        passwordRecovery.markAsUsed();
        passwordRecoveryRepository.save(passwordRecovery);
        userAccount.changePassword(new PasswordHash(passwordHasher.encode(command.newPassword())), now);
        userAccountRepository.save(userAccount);
    }
}
