package com.vanguardtech.storepulse.platform.safety.application.internal.outboundservices.acl;

import com.vanguardtech.storepulse.platform.assets.interfaces.acl.AssetsContextFacade;
import com.vanguardtech.storepulse.platform.propertycommunication.interfaces.acl.PropertyCommunicationContextFacade;
import com.vanguardtech.storepulse.platform.propertymanagement.interfaces.acl.CommercialUnitSummary;
import com.vanguardtech.storepulse.platform.propertymanagement.interfaces.acl.PropertyManagementContextFacade;
import com.vanguardtech.storepulse.platform.safety.domain.model.aggregates.Alert;
import com.vanguardtech.storepulse.platform.safety.domain.model.valueobjects.SensorType;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
public class ExternalAlertNotificationService {

    private static final ZoneId ZONE = ZoneId.of("America/Lima");
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm");

    private final AssetsContextFacade assetsContextFacade;
    private final PropertyManagementContextFacade propertyManagementContextFacade;
    private final PropertyCommunicationContextFacade propertyCommunicationContextFacade;

    public ExternalAlertNotificationService(AssetsContextFacade assetsContextFacade,
                                            PropertyManagementContextFacade propertyManagementContextFacade,
                                            PropertyCommunicationContextFacade propertyCommunicationContextFacade) {
        this.assetsContextFacade = assetsContextFacade;
        this.propertyManagementContextFacade = propertyManagementContextFacade;
        this.propertyCommunicationContextFacade = propertyCommunicationContextFacade;
    }

    public void notifyAlert(Alert alert) {
        assetsContextFacade.fetchResourceIdByIoTDeviceId(alert.getDeviceId())
                .flatMap(propertyManagementContextFacade::fetchUnitByResourceId)
                .ifPresent(unit -> notifyTenantOf(unit, alert));
    }

    private void notifyTenantOf(CommercialUnitSummary unit, Alert alert) {
        var administratorId = propertyManagementContextFacade.fetchAdministratorIdByGalleryId(unit.galleryId());
        var tenantId = propertyCommunicationContextFacade.fetchActiveTenantIdByUnitId(unit.id());
        if (administratorId.isEmpty() || tenantId.isEmpty()) return;
        propertyCommunicationContextFacade.notifyTenant(tenantId.get(), unit.id(), administratorId.get(),
                typeOf(alert.getSensorType()), titleOf(alert.getSensorType()), bodyOf(unit, alert), alert.getId());
    }

    private String typeOf(SensorType sensorType) {
        return switch (sensorType) {
            case MOTION, SMOKE, TEMPERATURE -> "EMERGENCY_ALERT";
            case HUMIDITY, ELECTRICITY, WATER -> "INCIDENT";
        };
    }

    private String titleOf(SensorType sensorType) {
        return switch (sensorType) {
            case MOTION -> "Intrusión detectada";
            case SMOKE -> "Humo detectado";
            case TEMPERATURE -> "Temperatura alta";
            case HUMIDITY -> "Humedad alta";
            case ELECTRICITY -> "Consumo eléctrico elevado";
            case WATER -> "Consumo de agua elevado";
        };
    }

    private String bodyOf(CommercialUnitSummary unit, Alert alert) {
        var detectedAt = alert.getDetectedAt().atZone(ZONE);
        var place = (unit.store() ? "el local " : "el área común ") + unit.code();
        var event = switch (alert.getSensorType()) {
            case MOTION -> "Se detectó movimiento fuera del horario de atención";
            case SMOKE -> "Se detectó humo";
            case TEMPERATURE -> "Se registró una temperatura alta";
            case HUMIDITY -> "Se registró una humedad alta";
            case ELECTRICITY -> "Se registró un consumo eléctrico elevado";
            case WATER -> "Se registró un consumo de agua elevado";
        };
        return "%s en %s el %s a las %s.".formatted(event, place, DATE.format(detectedAt), TIME.format(detectedAt));
    }
}
