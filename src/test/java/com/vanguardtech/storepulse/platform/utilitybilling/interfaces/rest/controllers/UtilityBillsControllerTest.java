package com.vanguardtech.storepulse.platform.utilitybilling.interfaces.rest.controllers;

import com.vanguardtech.storepulse.platform.utilitybilling.application.internal.outboundservices.acl.ExternalIamService;
import com.vanguardtech.storepulse.platform.utilitybilling.application.internal.outboundservices.acl.ExternalPropertyService;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.exceptions.UtilityBillingError;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.commands.GenerateUtilityBillCommand;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.queries.GetUtilityBillByIdQuery;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.services.UtilityBillCommandService;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.services.UtilityBillQueryService;
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

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UtilityBillsController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(UtilityBillsControllerTest.MethodSecurity.class)
class UtilityBillsControllerTest {

    private static final String REQUEST =
            "{\"unitId\":1,\"period\":\"2026-10\"}";

    @TestConfiguration
    @EnableMethodSecurity
    static class MethodSecurity {
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UtilityBillCommandService utilityBillCommandService;

    @MockitoBean
    private UtilityBillQueryService utilityBillQueryService;

    @MockitoBean
    private ExternalPropertyService externalPropertyService;

    @MockitoBean
    private ExternalIamService externalIamService;

    @Test
    @WithMockUser(roles = "GALLERY_ADMINISTRATOR")
    void answers409WhenTheMonthHasNoConsumption() throws Exception {
        when(utilityBillCommandService.handle(any(GenerateUtilityBillCommand.class)))
                .thenThrow(new BusinessRuleException(UtilityBillingError.NO_CONSUMPTION_DATA));

        mockMvc.perform(post("/api/v1/utility-bills").contentType(MediaType.APPLICATION_JSON).content(REQUEST))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("no_consumption_data"));
    }

    @Test
    @WithMockUser(roles = "TENANT")
    void forbidsATenantFromGeneratingBills() throws Exception {
        mockMvc.perform(post("/api/v1/utility-bills").contentType(MediaType.APPLICATION_JSON).content(REQUEST))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "TENANT")
    void answers404ForAnUnknownBill() throws Exception {
        when(utilityBillQueryService.handle(any(GetUtilityBillByIdQuery.class))).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/utility-bills/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("not_found"));
    }
}