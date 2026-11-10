package com.vanguardtech.storepulse.platform.utilitybilling.domain.model.aggregates;

import com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.entities.BillingItem;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.valueobjects.BillingPeriod;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.valueobjects.UtilityBillStatus;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.valueobjects.UtilityType;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UtilityBillTest {

    private static UtilityBill billWithCompleteness(int dataCompleteness) {
        var electricity = new BillingItem(UtilityType.ELECTRICITY, new BigDecimal("100"), new BigDecimal("90"),
                new BigDecimal("0.78"));
        var bill = new UtilityBill(1L, "A-01", "Local A-01", "Tienda Sol",
                BillingPeriod.ofMonth(YearMonth.of(2026, 10)), List.of(electricity), dataCompleteness);
        ReflectionTestUtils.setField(bill, "id", 1L);
        return bill;
    }

    @Test
    void totalsItsBreakdownAndBuildsItsCode() {
        var bill = billWithCompleteness(100);

        assertEquals(new BigDecimal("78.00"), bill.getTotalAmount());
        assertEquals("FAC-202610-A01", bill.getCode());
        assertEquals(UtilityBillStatus.DRAFT, bill.getStatus());
    }

    @Test
    void needsTheAdministratorAcceptanceToVerifyIncompleteData() {
        var bill = billWithCompleteness(50);

        var error = assertThrows(BusinessRuleException.class, () -> bill.verifyData(false));
        assertEquals("incomplete_data_not_accepted", error.getMessage());

        bill.verifyData(true);
        assertEquals(UtilityBillStatus.DATA_VERIFIED, bill.getStatus());
    }

    @Test
    void isDueFifteenDaysAfterItIsIssued() {
        var bill = billWithCompleteness(100);
        bill.verifyData(false);

        bill.issue(Instant.parse("2026-11-01T15:00:00Z"));

        assertEquals(UtilityBillStatus.ISSUED, bill.getStatus());
        assertEquals(LocalDate.of(2026, 11, 16), bill.getDueDate());
    }

    @Test
    void cannotBeIssuedBeforeItsDataIsVerified() {
        var bill = billWithCompleteness(100);

        var error = assertThrows(BusinessRuleException.class, () -> bill.issue(Instant.now()));
        assertEquals("invalid_transition", error.getMessage());
    }
}