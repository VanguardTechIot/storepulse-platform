package com.vanguardtech.storepulse.platform.assets.application.internal.commandservices;

import com.vanguardtech.storepulse.platform.assets.application.internal.outboundservices.acl.ExternalPropertyManagementService;
import com.vanguardtech.storepulse.platform.assets.domain.model.aggregates.Asset;
import com.vanguardtech.storepulse.platform.assets.domain.model.aggregates.Resource;
import com.vanguardtech.storepulse.platform.assets.domain.model.commands.DeactivateIoTDeviceCommand;
import com.vanguardtech.storepulse.platform.assets.domain.model.commands.RegisterIoTDeviceCommand;
import com.vanguardtech.storepulse.platform.assets.domain.model.entities.IoTDevice;
import com.vanguardtech.storepulse.platform.assets.domain.model.valueobjects.IoTDeviceType;
import com.vanguardtech.storepulse.platform.assets.domain.model.valueobjects.Location;
import com.vanguardtech.storepulse.platform.assets.infrastructure.persistence.jpa.repositories.AssetRepository;
import com.vanguardtech.storepulse.platform.assets.infrastructure.persistence.jpa.repositories.ResourceRepository;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssetCommandServiceImplTest {

    @Mock
    private AssetRepository assetRepository;
    @Mock
    private ResourceRepository resourceRepository;
    @Mock
    private ExternalPropertyManagementService externalPropertyManagementService;

    @InjectMocks
    private AssetCommandServiceImpl service;

    @Test
    void installsADeviceOnlyOnAResourceOfTheAdministratorsGallery() {
        var resource = new Resource("Stand A-01", null, new Location("Av. Lima 123", "1", null));
        ReflectionTestUtils.setField(resource, "id", 4L);
        when(resourceRepository.findById(4L)).thenReturn(Optional.of(resource));
        when(externalPropertyManagementService.isResourceOfAdministrator(4L, 8L)).thenReturn(false);
        when(externalPropertyManagementService.isResourceOfAdministrator(4L, 7L)).thenReturn(true);
        when(assetRepository.save(any(Asset.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var error = assertThrows(ResourceNotFoundException.class,
                () -> service.handle(new RegisterIoTDeviceCommand("sp-001", IoTDeviceType.INTRUSION_NODE, 4L, 8L)));
        var asset = service.handle(new RegisterIoTDeviceCommand("sp-001", IoTDeviceType.INTRUSION_NODE, 4L, 7L));

        assertEquals("resource_not_found", error.getMessage());
        assertEquals("IoT node Stand A-01", asset.orElseThrow().getName());
    }

    @Test
    void refusesToDeactivateADeviceOfAnotherGallery() {
        var asset = new Asset(4L, "IoT node Stand A-01", new IoTDevice("SP-001", IoTDeviceType.INTRUSION_NODE, null, null));
        when(assetRepository.findByIotDevice_Id(1L)).thenReturn(Optional.of(asset));
        when(externalPropertyManagementService.isResourceOfAdministrator(4L, 8L)).thenReturn(false);

        var error = assertThrows(ResourceNotFoundException.class,
                () -> service.handle(new DeactivateIoTDeviceCommand(1L, 8L, "maintenance")));

        assertEquals("iot_device_not_found", error.getMessage());
        verify(assetRepository, never()).save(any());
    }
}