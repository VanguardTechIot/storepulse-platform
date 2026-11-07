package com.vanguardtech.storepulse.platform.dashboardanalytics.application.internal.outboundservices.acl;

import com.vanguardtech.storepulse.platform.safety.interfaces.acl.SafetyContextFacade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExternalSafetyServiceTest {

    @Mock
    private SafetyContextFacade safetyContextFacade;

    @InjectMocks
    private ExternalSafetyService externalSafetyService;

    @Test
    void countsMissingStatusesAsZero() {
        when(safetyContextFacade.countAlertsByStatus()).thenReturn(Map.of("ACTIVE", 2L));
        when(safetyContextFacade.countOpenAlertsBySensorType()).thenReturn(Map.of("SMOKE", 2L));

        var summary = externalSafetyService.fetchAlertsSummary();

        assertEquals(2, summary.open());
        assertEquals(0, summary.acknowledged());
        assertEquals(0, summary.resolved());
        assertEquals(Map.of("SMOKE", 2L), summary.openBySensorType());
    }
}