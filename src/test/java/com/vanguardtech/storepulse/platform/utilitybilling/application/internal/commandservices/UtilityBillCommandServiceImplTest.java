package com.vanguardtech.storepulse.platform.utilitybilling.application.internal.commandservices;

import com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException;
import com.vanguardtech.storepulse.platform.utilitybilling.application.internal.outboundservices.acl.ExternalMonitoringService;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.aggregates.UtilityBill;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.aggregates.UtilityTariff;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.commands.GenerateUtilityBillCommand;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.valueobjects.ConsumptionSummary;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.valueobjects.UtilityType;
import com.vanguardtech.storepulse.platform.utilitybilling.infrastructure.persistence.jpa.repositories.UtilityBillRepository;
import com.vanguardtech.storepulse.platform.utilitybilling.infrastructure.persistence.jpa.repositories.UtilityTariffRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UtilityBillCommandServiceImplTest {

    private static final YearMonth OCTOBER = YearMonth.of(2026, 10);
    private static final GenerateUtilityBillCommand COMMAND =
            new GenerateUtilityBillCommand(1L, "A-01", "Local A-01", "Tienda Sol", OCTOBER);

    @Mock
    private UtilityBillRepository billRepository;

    @Mock
    private UtilityTariffRepository tariffRepository;

    @Mock
    private ExternalMonitoringService externalMonitoringService;

    @InjectMocks
    private UtilityBillCommandServiceImpl service;

    @Test
    void rejectsAMonthWithoutConsumption() {
        when(externalMonitoringService.fetchConsumption(any(), any(), any())).thenReturn(Optional.empty());

        var error = assertThrows(BusinessRuleException.class, () -> service.handle(COMMAND));
        assertEquals("no_consumption_data", error.getMessage());
    }

    @Test
    void billsOnlyTheUtilitiesWithConsumptionAndLowersTheCompleteness() {
        when(externalMonitoringService.fetchConsumption(1L, UtilityType.ELECTRICITY, OCTOBER))
                .thenReturn(Optional.of(new ConsumptionSummary(UtilityType.ELECTRICITY, new BigDecimal("100"))));
        when(externalMonitoringService.fetchConsumption(1L, UtilityType.WATER, OCTOBER)).thenReturn(Optional.empty());
        when(externalMonitoringService.fetchBaseline(1L, UtilityType.ELECTRICITY, OCTOBER)).thenReturn(Optional.empty());
        when(tariffRepository.findFirstByUtilityTypeAndEffectiveFromLessThanEqualOrderByEffectiveFromDesc(
                eq(UtilityType.ELECTRICITY), any()))
                .thenReturn(Optional.of(new UtilityTariff(UtilityType.ELECTRICITY, new BigDecimal("0.78"), LocalDate.EPOCH)));
        when(billRepository.save(any())).thenAnswer(invocation -> {
            UtilityBill bill = invocation.getArgument(0);
            ReflectionTestUtils.setField(bill, "id", 1L);
            return bill;
        });

        var bill = service.handle(COMMAND);

        assertEquals(50, bill.getDataCompleteness());
        assertEquals(new BigDecimal("78.00"), bill.getTotalAmount());
    }

    @Test
    void rejectsASecondBillForTheSameMonth() {
        when(billRepository.existsByUnitIdAndBillingPeriod_StartDate(1L, OCTOBER.atDay(1))).thenReturn(true);

        var error = assertThrows(BusinessRuleException.class, () -> service.handle(COMMAND));
        assertEquals("duplicate", error.getMessage());
    }
}