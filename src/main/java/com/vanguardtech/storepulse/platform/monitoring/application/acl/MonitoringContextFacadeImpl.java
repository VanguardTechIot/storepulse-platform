package com.vanguardtech.storepulse.platform.monitoring.application.acl;

import com.vanguardtech.storepulse.platform.monitoring.domain.model.aggregates.Consumption;
import com.vanguardtech.storepulse.platform.monitoring.domain.model.queries.GetAllConsumptionQuery;
import com.vanguardtech.storepulse.platform.monitoring.domain.model.queries.GetConsumptionByResourceIdQuery;
import com.vanguardtech.storepulse.platform.monitoring.domain.model.queries.GetConsumptionByResourceIdsQuery;
import com.vanguardtech.storepulse.platform.monitoring.domain.model.valueobjects.MeasurementType;
import com.vanguardtech.storepulse.platform.monitoring.domain.services.ConsumptionQueryService;
import com.vanguardtech.storepulse.platform.monitoring.interfaces.acl.MonitoringContextFacade;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.YearMonth;
import java.util.Collection;

@Service
public class MonitoringContextFacadeImpl implements MonitoringContextFacade {

    private final ConsumptionQueryService consumptionQueryService;

    public MonitoringContextFacadeImpl(ConsumptionQueryService consumptionQueryService) {
        this.consumptionQueryService = consumptionQueryService;
    }

    @Override
    public Double fetchCurrentMonthConsumption(String measurementType) {
        var type = MeasurementType.valueOf(measurementType);
        var currentPeriod = Consumption.startOf(Consumption.periodOf(Instant.now()));
        return consumptionQueryService.handle(new GetAllConsumptionQuery()).stream()
                .filter(consumption -> consumption.getMeasurementType() == type
                        && consumption.getPeriodStart().equals(currentPeriod))
                .mapToDouble(Consumption::getTotalValue)
                .sum();
    }

    @Override
    public Double fetchCurrentMonthConsumption(Collection<Long> resourceIds, String measurementType) {
        var type = MeasurementType.valueOf(measurementType);
        var currentPeriod = Consumption.startOf(Consumption.periodOf(Instant.now()));
        return consumptionQueryService.handle(new GetConsumptionByResourceIdsQuery(resourceIds)).stream()
                .filter(consumption -> consumption.getMeasurementType() == type
                        && consumption.getPeriodStart().equals(currentPeriod))
                .mapToDouble(Consumption::getTotalValue)
                .sum();
    }

    @Override
    public Double fetchMonthlyConsumption(Long resourceId, String measurementType, YearMonth month) {
        var type = MeasurementType.valueOf(measurementType);
        var periodStart = Consumption.startOf(month);
        return consumptionQueryService.handle(new GetConsumptionByResourceIdQuery(resourceId)).stream()
                .filter(consumption -> consumption.getMeasurementType() == type
                        && consumption.getPeriodStart().equals(periodStart))
                .findFirst()
                .map(Consumption::getTotalValue)
                .orElse(null);
    }
}