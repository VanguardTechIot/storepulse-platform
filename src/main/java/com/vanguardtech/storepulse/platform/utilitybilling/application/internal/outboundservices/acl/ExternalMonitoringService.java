package com.vanguardtech.storepulse.platform.utilitybilling.application.internal.outboundservices.acl;

import com.vanguardtech.storepulse.platform.monitoring.domain.model.aggregates.Consumption;
import com.vanguardtech.storepulse.platform.monitoring.domain.model.queries.GetConsumptionByResourceIdQuery;
import com.vanguardtech.storepulse.platform.monitoring.domain.model.valueobjects.MeasurementType;
import com.vanguardtech.storepulse.platform.monitoring.domain.services.ConsumptionQueryService;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.valueobjects.ConsumptionSummary;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.valueobjects.UtilityType;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.Optional;

/**
 * Reads from Monitoring the consumption that supports a bill. Until Property Management is
 * available, the unit is identified by the resource that Monitoring records its consumption against.
 * This is the only class of Utility Billing that knows the Monitoring model.
 */
@Service("utilityBillingExternalMonitoringService")
public class ExternalMonitoringService {

    private final ConsumptionQueryService consumptionQueryService;

    public ExternalMonitoringService(ConsumptionQueryService consumptionQueryService) {
        this.consumptionQueryService = consumptionQueryService;
    }

    /** Consumption of the utility in the month, or empty when Monitoring has no record for it. */
    public Optional<ConsumptionSummary> fetchConsumption(Long unitId, UtilityType utilityType, YearMonth period) {
        var type = MeasurementType.valueOf(utilityType.name());
        var periodStart = Consumption.startOf(period);
        return consumptionQueryService.handle(new GetConsumptionByResourceIdQuery(unitId)).stream()
                .filter(consumption -> consumption.getMeasurementType() == type
                        && consumption.getPeriodStart().equals(periodStart))
                .findFirst()
                .map(consumption -> new ConsumptionSummary(utilityType,
                        BigDecimal.valueOf(consumption.getTotalValue()).setScale(3, RoundingMode.HALF_UP)));
    }
}