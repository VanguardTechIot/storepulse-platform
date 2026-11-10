package com.vanguardtech.storepulse.platform.utilitybilling.application.internal.commandservices;

import com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.ResourceNotFoundException;
import com.vanguardtech.storepulse.platform.utilitybilling.application.internal.outboundservices.acl.ExternalIamService;
import com.vanguardtech.storepulse.platform.utilitybilling.application.internal.outboundservices.acl.ExternalPropertyService;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.exceptions.UtilityBillingError;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.aggregates.BillingDispute;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.commands.RaiseBillingDisputeCommand;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.commands.ResolveBillingDisputeCommand;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.commands.ReviewBillingDisputeCommand;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.valueobjects.BillingDisputeStatus;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.services.BillingDisputeCommandService;
import com.vanguardtech.storepulse.platform.utilitybilling.infrastructure.persistence.jpa.repositories.BillingDisputeRepository;
import com.vanguardtech.storepulse.platform.utilitybilling.infrastructure.persistence.jpa.repositories.UtilityBillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class BillingDisputeCommandServiceImpl implements BillingDisputeCommandService {

    private final BillingDisputeRepository disputeRepository;
    private final UtilityBillRepository billRepository;
    private final ExternalIamService externalIamService;
    private final ExternalPropertyService externalPropertyService;

    /**
     * Registers the dispute and puts its bill in dispute, so only an issued bill can be disputed.
     * The tenant must occupy the billed store.
     */
    @Override
    @Transactional
    public BillingDispute handle(RaiseBillingDisputeCommand command) {
        var tenantId = externalIamService.fetchAuthenticatedUserId()
                .orElseThrow(() -> new BusinessRuleException(UtilityBillingError.TENANT_REQUIRED));
        var bill = billRepository.findById(command.utilityBillId())
                .filter(found -> externalPropertyService.isUnitOfTenant(found.getUnitId(), tenantId))
                .orElseThrow(() -> new ResourceNotFoundException(UtilityBillingError.NOT_FOUND));
        bill.markInDispute();
        billRepository.save(bill);
        var dispute = disputeRepository.save(new BillingDispute(command.utilityBillId(), tenantId,
                command.reason(), command.description()));
        dispute.raise();
        return disputeRepository.save(dispute);
    }

    @Override
    public BillingDispute handle(ReviewBillingDisputeCommand command) {
        var dispute = findDisputeOfAdministrator(command.disputeId(), command.administratorId());
        dispute.review(command.reviewNotes());
        return disputeRepository.save(dispute);
    }

    /** Closes the dispute. Its bill goes back to resolved once none of its disputes remains open. */
    @Override
    @Transactional
    public BillingDispute handle(ResolveBillingDisputeCommand command) {
        var dispute = findDisputeOfAdministrator(command.disputeId(), command.administratorId());
        dispute.resolve(command.resolution(), Instant.now());
        var saved = disputeRepository.save(dispute);
        var stillOpen = disputeRepository.existsByUtilityBillIdAndStatusNot(
                saved.getUtilityBillId(), BillingDisputeStatus.RESOLVED);
        if (!stillOpen) {
            var bill = billRepository.findById(saved.getUtilityBillId())
                    .orElseThrow(() -> new ResourceNotFoundException(UtilityBillingError.NOT_FOUND));
            bill.markResolved();
            billRepository.save(bill);
        }
        return saved;
    }

    /** A dispute over a bill of another administrator's gallery is answered as not found. */
    private BillingDispute findDisputeOfAdministrator(Long disputeId, Long administratorId) {
        return disputeRepository.findById(disputeId)
                .filter(dispute -> billRepository.findById(dispute.getUtilityBillId())
                        .map(bill -> externalPropertyService.isUnitOfAdministrator(bill.getUnitId(), administratorId))
                        .orElse(false))
                .orElseThrow(() -> new ResourceNotFoundException(UtilityBillingError.NOT_FOUND));
    }
}