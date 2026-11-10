package com.vanguardtech.storepulse.platform.propertymanagement.interfaces.rest.controllers;

import com.vanguardtech.storepulse.platform.propertymanagement.application.internal.outboundservices.acl.ExternalIamService;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.exceptions.PropertyManagementError;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.aggregates.CommercialGallery;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.commands.RegisterCommercialGalleryCommand;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.commands.RegisterCommercialUnitCommand;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.commands.UpdateCommercialGalleryInfoCommand;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.queries.GetCommercialGalleryByAdministratorIdQuery;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.services.CommercialGalleryCommandService;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.services.CommercialGalleryQueryService;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.services.CommercialUnitCommandService;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.services.CommercialUnitQueryService;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.ResourceNotFoundException;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GalleriesController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GalleriesControllerTest.MethodSecurity.class)
class GalleriesControllerTest {

    private static final String GALLERY_REQUEST =
            "{\"name\":\"Galería Central\",\"address\":\"Jr. de la Unión 845\",\"totalUnits\":40}";
    private static final String UNIT_REQUEST =
            "{\"type\":\"STORE\",\"code\":\"A-03\",\"floor\":\"1\",\"areaSquareMeters\":20}";

    @TestConfiguration
    @EnableMethodSecurity
    static class MethodSecurity {
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CommercialGalleryCommandService galleryCommandService;

    @MockitoBean
    private CommercialGalleryQueryService galleryQueryService;

    @MockitoBean
    private CommercialUnitCommandService unitCommandService;

    @MockitoBean
    private CommercialUnitQueryService unitQueryService;

    @MockitoBean
    private ExternalIamService externalIamService;

    @Test
    @WithMockUser(roles = "GALLERY_ADMINISTRATOR")
    void registersTheGalleryOfTheAuthenticatedAdministrator() throws Exception {
        when(externalIamService.fetchAuthenticatedAdministratorId()).thenReturn(7L);
        when(galleryCommandService.handle(any(RegisterCommercialGalleryCommand.class)))
                .thenReturn(new CommercialGallery(7L, "Galería Central", "Jr. de la Unión 845", 40));

        mockMvc.perform(post("/api/v1/galleries").contentType(MediaType.APPLICATION_JSON).content(GALLERY_REQUEST))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.administratorId").value(7))
                .andExpect(jsonPath("$.name").value("Galería Central"));
    }

    @Test
    @WithMockUser(roles = "GALLERY_ADMINISTRATOR")
    void answers409WhenTheAdministratorAlreadyHasAGallery() throws Exception {
        when(externalIamService.fetchAuthenticatedAdministratorId()).thenReturn(7L);
        when(galleryCommandService.handle(any(RegisterCommercialGalleryCommand.class)))
                .thenThrow(new BusinessRuleException(PropertyManagementError.GALLERY_ALREADY_REGISTERED));

        mockMvc.perform(post("/api/v1/galleries").contentType(MediaType.APPLICATION_JSON).content(GALLERY_REQUEST))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("gallery_already_registered"));
    }

    @Test
    @WithMockUser(roles = "TENANT")
    void forbidsATenantFromRegisteringUnits() throws Exception {
        mockMvc.perform(post("/api/v1/galleries/1/units").contentType(MediaType.APPLICATION_JSON).content(UNIT_REQUEST))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "GALLERY_ADMINISTRATOR")
    void answers409ForADuplicatedUnitCode() throws Exception {
        when(unitCommandService.handle(any(RegisterCommercialUnitCommand.class)))
                .thenThrow(new BusinessRuleException(PropertyManagementError.DUPLICATED_UNIT_CODE));

        mockMvc.perform(post("/api/v1/galleries/1/units").contentType(MediaType.APPLICATION_JSON).content(UNIT_REQUEST))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("duplicated_unit_code"));
    }

    @Test
    @WithMockUser(roles = "GALLERY_ADMINISTRATOR")
    void answers404WhenTheGalleryBelongsToAnotherAdministrator() throws Exception {
        when(externalIamService.fetchAuthenticatedAdministratorId()).thenReturn(8L);
        when(galleryCommandService.handle(any(UpdateCommercialGalleryInfoCommand.class)))
                .thenThrow(new ResourceNotFoundException(PropertyManagementError.GALLERY_NOT_FOUND));

        mockMvc.perform(put("/api/v1/galleries/1").contentType(MediaType.APPLICATION_JSON).content(GALLERY_REQUEST))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("gallery_not_found"));
    }

    @Test
    @WithMockUser(roles = "GALLERY_ADMINISTRATOR")
    void answersAnEmptyListWhenTheAdministratorHasNoGallery() throws Exception {
        when(externalIamService.fetchAuthenticatedAdministratorId()).thenReturn(7L);
        when(galleryQueryService.handle(any(GetCommercialGalleryByAdministratorIdQuery.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/galleries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @WithMockUser(roles = "GALLERY_ADMINISTRATOR")
    void doesNotShowTheUnitsOfAnotherAdministratorGallery() throws Exception {
        when(externalIamService.fetchAuthenticatedAdministratorId()).thenReturn(8L);
        when(galleryQueryService.handle(any(GetCommercialGalleryByAdministratorIdQuery.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/galleries/1/units"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("gallery_not_found"));
    }
}