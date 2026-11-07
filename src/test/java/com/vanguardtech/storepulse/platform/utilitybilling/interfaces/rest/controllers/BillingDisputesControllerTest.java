package com.vanguardtech.storepulse.platform.utilitybilling.interfaces.rest.controllers;

import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.aggregates.BillingDispute;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.commands.RaiseBillingDisputeCommand;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.valueobjects.DisputeReason;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.services.BillingDisputeCommandService;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.services.BillingDisputeQueryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BillingDisputesController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(BillingDisputesControllerTest.MethodSecurity.class)
class BillingDisputesControllerTest {

    @TestConfiguration
    @EnableMethodSecurity
    static class MethodSecurity {
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BillingDisputeCommandService billingDisputeCommandService;

    @MockitoBean
    private BillingDisputeQueryService billingDisputeQueryService;

    @Test
    @WithMockUser(roles = "TENANT")
    void letsATenantRaiseADispute() throws Exception {
        when(billingDisputeCommandService.handle(any(RaiseBillingDisputeCommand.class)))
                .thenReturn(new BillingDispute(1L, 2L, DisputeReason.RATE, "Rate too high"));

        mockMvc.perform(post("/api/v1/billing-disputes").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"utilityBillId\":1,\"reason\":\"RATE\",\"description\":\"Rate too high\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("OPEN"));
    }

    @Test
    @WithMockUser(roles = "TENANT")
    void forbidsATenantFromResolvingADispute() throws Exception {
        mockMvc.perform(patch("/api/v1/billing-disputes/1/resolve").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"resolution\":\"Done\"}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("access_denied"));
    }

    @Test
    @WithMockUser(roles = "GALLERY_ADMINISTRATOR")
    void rejectsAResolutionWithoutText() throws Exception {
        mockMvc.perform(patch("/api/v1/billing-disputes/1/resolve").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"resolution\":\"\"}"))
                .andExpect(status().isBadRequest());
    }
}