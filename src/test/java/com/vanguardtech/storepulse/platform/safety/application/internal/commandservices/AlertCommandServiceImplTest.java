package com.vanguardtech.storepulse.platform.safety.application.internal.commandservices;

import com.vanguardtech.storepulse.platform.safety.application.internal.outboundservices.acl.ExternalAssetsService;
import com.vanguardtech.storepulse.platform.safety.domain.model.aggregates.Alert;
import com.vanguardtech.storepulse.platform.safety.domain.model.commands.AcknowledgeAlertCommand;
import com.vanguardtech.storepulse.platform.safety.domain.model.valueobjects.AlertStatus;
import com.vanguardtech.storepulse.platform.safety.domain.model.valueobjects.SensorType;
import com.vanguardtech.storepulse.platform.safety.infrastructure.persistence.jpa.repositories.AlertRepository;
import com.vanguardtech.storepulse.platform.safety.infrastructure.persistence.jpa.repositories.DeviceStateRepository;
import com.vanguardtech.storepulse.platform.safety.infrastructure.persistence.jpa.repositories.SafetyRuleRepository;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlertCommandServiceImplTest {

    @Mock
    private AlertRepository alertRepository;
    @Mock
    private DeviceStateRepository deviceStateRepository;
    @Mock
    private SafetyRuleRepository safetyRuleRepository;
    @Mock
    private ExternalAssetsService externalAssetsService;

    @InjectMocks
    private AlertCommandServiceImpl service;

    private static Alert smokeAlertOfDevice(Long deviceId) {
        return new Alert(1L, deviceId, 20L, SensorType.SMOKE, 0.8, Instant.now());
    }

    @Test
    void acknowledgesAnAlertOfTheAdministratorGallery() {
        when(alertRepository.findById(4L)).thenReturn(Optional.of(smokeAlertOfDevice(3L)));
        when(externalAssetsService.fetchDeviceIdsVisibleToAuthenticatedUser()).thenReturn(List.of(3L));
        when(alertRepository.save(any(Alert.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var alert = service.handle(new AcknowledgeAlertCommand(4L));

        assertEquals(AlertStatus.ACKNOWLEDGED, alert.getStatus());
    }

    @Test
    void doesNotAcknowledgeAnAlertOfAnotherGallery() {
        when(alertRepository.findById(4L)).thenReturn(Optional.of(smokeAlertOfDevice(9L)));
        when(externalAssetsService.fetchDeviceIdsVisibleToAuthenticatedUser()).thenReturn(List.of(3L));

        assertThrows(ResourceNotFoundException.class, () -> service.handle(new AcknowledgeAlertCommand(4L)));
        verify(alertRepository, never()).save(any());
    }
}