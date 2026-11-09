package com.vanguardtech.storepulse.platform.safety.domain.model.aggregates;

import com.vanguardtech.storepulse.platform.safety.domain.model.valueobjects.AlertStatus;
import com.vanguardtech.storepulse.platform.safety.domain.model.valueobjects.SensorType;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AlertTest {

    private static Alert newAlert() {
        return new Alert(1L, 3L, 20L, SensorType.SMOKE, 0.8, Instant.now());
    }

    @Test
    void isResolvedOnlyAfterBeingAcknowledged() {
        var alert = newAlert();
        assertThrows(BusinessRuleException.class, () -> alert.resolve("Checked"));

        alert.acknowledge();
        alert.resolve("  Checked  ");

        assertEquals(AlertStatus.RESOLVED, alert.getStatus());
        assertEquals("Checked", alert.getResolutionNote());
    }

    @Test
    void cannotBeAcknowledgedTwice() {
        var alert = newAlert();
        alert.acknowledge();

        assertThrows(BusinessRuleException.class, alert::acknowledge);
    }
}