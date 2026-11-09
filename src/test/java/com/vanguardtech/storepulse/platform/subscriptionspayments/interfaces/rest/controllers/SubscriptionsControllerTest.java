package com.vanguardtech.storepulse.platform.subscriptionspayments.interfaces.rest.controllers;

import com.vanguardtech.storepulse.platform.subscriptionspayments.application.internal.outboundservices.acl.ExternalIamService;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.exceptions.SubscriptionsError;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.commands.CancelRenewalCommand;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.queries.GetSubscriptionByGalleryIdQuery;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.services.SubscriptionCommandService;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.services.SubscriptionQueryService;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException;
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

@WebMvcTest(SubscriptionsController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(SubscriptionsControllerTest.MethodSecurity.class)
class SubscriptionsControllerTest {

    @TestConfiguration
    @EnableMethodSecurity
    static class MethodSecurity {
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SubscriptionCommandService subscriptionCommandService;

    @MockitoBean
    private SubscriptionQueryService subscriptionQueryService;

    @MockitoBean
    private ExternalIamService externalIamService;

    @Test
    @WithMockUser(roles = "GALLERY_ADMINISTRATOR")
    void answers404WhenTheGalleryHasNoSubscription() throws Exception {
        when(subscriptionQueryService.handle(any(GetSubscriptionByGalleryIdQuery.class))).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/subscriptions").param("galleryId", "1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("subscription_not_found"));
    }

    @Test
    @WithMockUser(roles = "GALLERY_ADMINISTRATOR")
    void rejectsARequestWithoutUnitCount() throws Exception {
        mockMvc.perform(post("/api/v1/subscriptions").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"galleryId\":1,\"planId\":1}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "TENANT")
    void forbidsATenantFromCancellingTheRenewal() throws Exception {
        mockMvc.perform(post("/api/v1/subscriptions/1/cancel-renewal"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("access_denied"));
    }

    @Test
    @WithMockUser(roles = "GALLERY_ADMINISTRATOR")
    void answers409WithTheBusinessRuleCode() throws Exception {
        when(subscriptionCommandService.handle(any(CancelRenewalCommand.class)))
                .thenThrow(new BusinessRuleException(SubscriptionsError.RENEWAL_ALREADY_DISABLED));

        mockMvc.perform(post("/api/v1/subscriptions/1/cancel-renewal"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("renewal_already_disabled"));
    }
}