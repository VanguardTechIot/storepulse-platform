package com.vanguardtech.storepulse.platform.utilitybilling.application.internal.commandservices;

import com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.ResourceNotFoundException;
import com.vanguardtech.storepulse.platform.utilitybilling.application.internal.outboundservices.acl.ExternalIamService;
import com.vanguardtech.storepulse.platform.utilitybilling.application.internal.outboundservices.acl.ExternalMonitoringService;
import com.vanguardtech.storepulse.platform.utilitybilling.application.internal.outboundservices.acl.ExternalPropertyService;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.exceptions.UtilityBillingError;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.aggregates.UtilityBill;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.commands.GenerateUtilityBillCommand;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.commands.IssueUtilityBillCommand;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.commands.VerifyBillingDataCommand;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.entities.BillingItem;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.valueobjects.BaselineConsumption;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.valueobjects.BillingPeriod;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.valueobjects.UtilityType;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.services.UtilityBillCommandService;
import com.vanguardtech.storepulse.platform.utilitybilling.infrastructure.persistence.jpa.repositories.UtilityBillRepository;
import com.vanguardtech.storepulse.platform.utilitybilling.infrastructure.persistence.jpa.repositories.UtilityTariffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class UtilityBillCommandServiceImpl implements UtilityBillCommandService {

    private final UtilityBillRepository billRepository;
    private final UtilityTariffRepository tariffRepository;
    private final ExternalMonitoringService externalMonitoringService;
    private final ExternalPropertyService externalPropertyService;
    private final ExternalIamService externalIamService;

    /**
     * Builds the breakdown from the consumption Monitoring recorded for the month on the resource
     * linked to the store. Utilities without consumption are left out and lower the data
     * completeness of the bill.
     */
    @Override
    @Transactional
    public UtilityBill handle(GenerateUtilityBillCommand command) {
        if (command.period() == null)
            throw new BusinessRuleException(UtilityBillingError.INVALID_PERIOD);
        var unit = externalPropertyService.fetchUnitOfAdministrator(command.unitId(), command.administratorId())
                .orElseThrow(() -> new ResourceNotFoundException(UtilityBillingError.UNIT_NOT_FOUND));
        var resourceId = externalPropertyService.fetchResourceIdByUnitId(unit.id())
                .orElseThrow(() -> new BusinessRuleException(UtilityBillingError.UNIT_NOT_MONITORED));
        var billingPeriod = BillingPeriod.ofMonth(command.period());
        if (billRepository.existsByUnitIdAndBillingPeriod_StartDate(command.unitId(), billingPeriod.startDate()))
            throw new BusinessRuleException(UtilityBillingError.DUPLICATE);

        var breakdown = new ArrayList<BillingItem>();
        for (var utilityType : UtilityType.values()) {
            var summary = externalMonitoringService.fetchConsumption(resourceId, utilityType, command.period());
            if (summary.isEmpty()) continue;
            var consumption = summary.get().consumption();
            var baseline = externalMonitoringService.fetchBaseline(resourceId, utilityType, command.period())
                    .map(BaselineConsumption::value)
                    .orElse(consumption);
            var tariff = tariffRepository
                    .findFirstByUtilityTypeAndEffectiveFromLessThanEqualOrderByEffectiveFromDesc(
                            utilityType, billingPeriod.startDate())
                    .orElseThrow(() -> new BusinessRuleException(UtilityBillingError.TARIFF_NOT_FOUND));
            breakdown.add(new BillingItem(utilityType, consumption, baseline, tariff.getRate()));
        }
        if (breakdown.isEmpty())
            throw new BusinessRuleException(UtilityBillingError.NO_CONSUMPTION_DATA);

        var dataCompleteness = breakdown.size() * UtilityBill.FULL_DATA_COMPLETENESS / UtilityType.values().length;
        var tenantName = externalPropertyService.fetchActiveTenantIdByUnitId(unit.id())
                .flatMap(externalIamService::fetchEmailByUserId)
                .orElse(null);
        var unitName = unit.businessName() != null ? unit.businessName() : unit.code();
        var bill = billRepository.save(new UtilityBill(unit.id(), unit.code(), unitName, tenantName, billingPeriod,
                breakdown, dataCompleteness));
        bill.generate();
        return billRepository.save(bill);
    }

    @Override
    public UtilityBill handle(VerifyBillingDataCommand command) {
        return update(command.utilityBillId(), command.administratorId(),
                bill -> bill.verifyData(command.acceptIncompleteData()));
    }

    @Override
    public UtilityBill handle(IssueUtilityBillCommand command) {
        return update(command.utilityBillId(), command.administratorId(), bill -> bill.issue(Instant.now()));
    }

    /** A bill of another administrator's gallery is answered as not found. */
    private UtilityBill update(Long utilityBillId, Long administratorId, Consumer<UtilityBill> change) {
        var bill = billRepository.findById(utilityBillId)
                .filter(found -> externalPropertyService.isUnitOfAdministrator(found.getUnitId(), administratorId))
                .orElseThrow(() -> new ResourceNotFoundException(UtilityBillingError.NOT_FOUND));
        change.accept(bill);
        return billRepository.save(bill);
    }
}