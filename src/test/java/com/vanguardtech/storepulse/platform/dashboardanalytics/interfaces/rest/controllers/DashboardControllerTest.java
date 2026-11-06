package com.vanguardtech.storepulse.platform.dashboardanalytics.interfaces.rest.controllers;

import com.vanguardtech.storepulse.platform.dashboardanalytics.domain.model.queries.GetDashboardSummaryQuery;
import com.vanguardtech.storepulse.platform.dashboardanalytics.domain.model.valueobjects.AlertsSummary;
import com.vanguardtech.storepulse.platform.dashboardanalytics.domain.model.valueobjects.ConsumptionSummary;
import com.vanguardtech.storepulse.platform.dashboardanalytics.domain.model.valueobjects.DashboardSummary;
import com.vanguardtech.storepulse.platform.dashboardanalytics.domain.services.DashboardQueryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DashboardController.class)
@AutoConfigureMockMvc(addFilters = false)
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DashboardQueryService dashboardQueryService;

    @Test
    @WithMockUser(roles = "TENANT")
    void answersTheSummaryOfAlertsAndConsumption() throws Exception {
        when(dashboardQueryService.handle(any(GetDashboardSummaryQuery.class))).thenReturn(new DashboardSummary(
                new AlertsSummary(1, 2, 5, Map.of("SMOKE", 3L)), new ConsumptionSummary(120.5, 3.2)));

        mockMvc.perform(get("/api/v1/dashboard/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.alerts.open").value(3))
                .andExpect(jsonPath("$.alerts.openBySensorType.SMOKE").value(3))
                .andExpect(jsonPath("$.consumption.electricityKwh").value(120.5));
    }
}