package com.vanguardtech.storepulse.platform.iam.interfaces.rest.transform;

import com.vanguardtech.storepulse.platform.iam.domain.model.valueobjects.Role;
import com.vanguardtech.storepulse.platform.iam.interfaces.rest.resources.SignUpResource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SignUpCommandFromResourceAssemblerTest {

    @Test
    void registersAGalleryAdministratorWhenTheRoleIsOmitted() {
        var command = SignUpCommandFromResourceAssembler.toCommandFromResource(
                new SignUpResource("Ana Torres", "ana@storepulse.pe", "Clave#2026", null));

        assertEquals(Role.GALLERY_ADMINISTRATOR, command.role());
    }

    @Test
    void registersATenantWhenItAsksForThatRole() {
        var command = SignUpCommandFromResourceAssembler.toCommandFromResource(
                new SignUpResource("Luis Rojas", "luis@storepulse.pe", "Clave#2026", Role.TENANT));

        assertEquals(Role.TENANT, command.role());
    }
}