package com.vanguardtech.storepulse.platform.utilitybilling.domain.model.aggregates;

import com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.valueobjects.BillingDisputeStatus;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.valueobjects.DisputeReason;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BillingDisputeTest {

    @Test
    void goesFromOpenToReviewAndThenToResolved() {
        var dispute = new BillingDispute(1L, 2L, DisputeReason.RATE, "The rate is higher than agreed");
        ReflectionTestUtils.setField(dispute, "id", 1L);

        dispute.review("Checking the tariff");
        assertEquals(BillingDisputeStatus.UNDER_REVIEW, dispute.getStatus());

        dispute.resolve("Rate corrected", Instant.now());
        assertEquals(BillingDisputeStatus.RESOLVED, dispute.getStatus());
    }

    @Test
    void cannotBeResolvedWithoutAResolution() {
        var dispute = new BillingDispute(1L, 2L, DisputeReason.OTHER, null);

        var error = assertThrows(BusinessRuleException.class, () -> dispute.resolve(" ", Instant.now()));
        assertEquals("resolution_required", error.getMessage());
    }
}