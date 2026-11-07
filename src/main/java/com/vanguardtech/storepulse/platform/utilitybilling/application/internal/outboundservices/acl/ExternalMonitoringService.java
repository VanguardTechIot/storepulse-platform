package com.vanguardtech.storepulse.platform.utilitybilling.application.internal.outboundservices.acl;

import com.vanguardtech.storepulse.platform.monitoring.interfaces.acl.MonitoringContextFacade;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.valueobjects.BaselineConsumption;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.valueobjects.ConsumptionSummary;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.valueobjects.UtilityType;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * Reads from Monitoring the consumption that supports a bill. Until Property Management is
 * available, the unit is identified by the resource that Monitoring records its consumption against.
 */
@Service("utilityBillingExternalMonitoringService")
public class ExternalMonitoringService {

    /** Number of months before the billed one that make up the baseline. */
    private static final int BASELINE_MONTHS = 3;

    private final MonitoringContextFacade monitoringContextFacade;

    public ExternalMonitoringService(MonitoringContextFacade monitoringContextFacade) {
        this.monitoringContextFacade = monitoringContextFacade;
    }

    /** Consumption of the utility in the month, or empty when Monitoring has no record for it. */
    public Optional<ConsumptionSummary> fetchConsumption(Long unitId, UtilityType utilityType, YearMonth period) {
        return Optional.ofNullable(monitoringContextFacade.fetchMonthlyConsumption(unitId, utilityType.name(), period))
                .map(value -> new ConsumptionSummary(utilityType, scaled(value)));
    }

    /**
     * Average monthly consumption over the months before the billed one that have a record, or
     * empty when there is no history.
     */
    public Optional<BaselineConsumption> fetchBaseline(Long unitId, UtilityType utilityType, YearMonth period) {
        return IntStream.rangeClosed(1, BASELINE_MONTHS)
                .mapToObj(monthsBack -> monitoringContextFacade.fetchMonthlyConsumption(
                        unitId, utilityType.name(), period.minusMonths(monthsBack)))
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .average()
                .stream()
                .mapToObj(average -> new BaselineConsumption(utilityType, scaled(average)))
                .findFirst();
    }

    private static BigDecimal scaled(double value) {
        return BigDecimal.valueOf(value).setScale(3, RoundingMode.HALF_UP);
    }
}