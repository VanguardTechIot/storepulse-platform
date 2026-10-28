package com.vanguardtech.storepulse.platform.monitoring.application.acl;

import com.vanguardtech.storepulse.platform.monitoring.domain.model.aggregates.Consumption;
import com.vanguardtech.storepulse.platform.monitoring.domain.model.queries.GetConsumptionByResourceIdQuery;
import com.vanguardtech.storepulse.platform.monitoring.domain.model.queries.GetSensorStatusByDeviceIdQuery;
import com.vanguardtech.storepulse.platform.monitoring.domain.model.valueobjects.MeasurementType;
import com.vanguardtech.storepulse.platform.monitoring.domain.model.valueobjects.SensorStatus;
import com.vanguardtech.storepulse.platform.monitoring.domain.services.ConsumptionQueryService;
import com.vanguardtech.storepulse.platform.monitoring.domain.services.SensorStatusQueryService;
import com.vanguardtech.storepulse.platform.monitoring.interfaces.acl.MonitoringContextFacade;
import org.springframework.stereotype.Service;

@Service
public class MonitoringContextFacadeImpl implements MonitoringContextFacade {

    private final SensorStatusQueryService sensorStatusQueryService;
    private final ConsumptionQueryService consumptionQueryService;

    public MonitoringContextFacadeImpl(SensorStatusQueryService sensorStatusQueryService,
                                       ConsumptionQueryService consumptionQueryService) {
        this.sensorStatusQueryService = sensorStatusQueryService;
        this.consumptionQueryService = consumptionQueryService;
    }

    @Override
    public Double fetchLatestMeasurementValue(Long deviceId, String measurementType) {
        var type = MeasurementType.valueOf(measurementType);
        return sensorStatusQueryService.handle(new GetSensorStatusByDeviceIdQuery(deviceId)).stream()
                .filter(status -> status.type() == type)
                .findFirst()
                .map(SensorStatus::value)
                .orElse(null);
    }

    @Override
    public Double fetchTotalConsumption(Long resourceId, String measurementType) {
        var type = MeasurementType.valueOf(measurementType);
        return consumptionQueryService.handle(new GetConsumptionByResourceIdQuery(resourceId)).stream()
                .filter(consumption -> consumption.getMeasurementType() == type)
                .mapToDouble(Consumption::getTotalValue)
                .sum();
    }
}
