package com.vanguardtech.storepulse.platform.monitoring.interfaces.rest.controllers;

import com.vanguardtech.storepulse.platform.monitoring.application.internal.outboundservices.acl.ExternalResourceAssetService;
import com.vanguardtech.storepulse.platform.monitoring.domain.model.aggregates.Telemetry;
import com.vanguardtech.storepulse.platform.monitoring.domain.model.queries.GetTelemetryByDeviceIdsQuery;
import com.vanguardtech.storepulse.platform.monitoring.domain.model.queries.GetTelemetryByIdQuery;
import com.vanguardtech.storepulse.platform.monitoring.domain.services.TelemetryCommandService;
import com.vanguardtech.storepulse.platform.monitoring.domain.services.TelemetryQueryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TelemetryController.class)
@AutoConfigureMockMvc(addFilters = false)
class TelemetryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TelemetryCommandService telemetryCommandService;

    @MockitoBean
    private TelemetryQueryService telemetryQueryService;

    @MockitoBean
    private ExternalResourceAssetService externalResourceAssetService;

    @Test
    @WithMockUser(roles = "TENANT")
    void doesNotListTheTelemetryOfADeviceOutsideTheUserStores() throws Exception {
        when(externalResourceAssetService.fetchDeviceIdsVisibleToAuthenticatedUser()).thenReturn(List.of(3L));
        when(telemetryQueryService.handle(new GetTelemetryByDeviceIdsQuery(List.of()))).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/telemetry").param("deviceId", "9"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
        verify(telemetryQueryService).handle(new GetTelemetryByDeviceIdsQuery(List.of()));
    }

    @Test
    @WithMockUser(roles = "GALLERY_ADMINISTRATOR")
    void answersNotFoundForTheTelemetryOfAnotherGallery() throws Exception {
        when(externalResourceAssetService.fetchDeviceIdsVisibleToAuthenticatedUser()).thenReturn(List.of(3L));
        when(telemetryQueryService.handle(new GetTelemetryByIdQuery(5L)))
                .thenReturn(Optional.of(new Telemetry(9L, Instant.now())));

        mockMvc.perform(get("/api/v1/telemetry/5")).andExpect(status().isNotFound());
    }
}