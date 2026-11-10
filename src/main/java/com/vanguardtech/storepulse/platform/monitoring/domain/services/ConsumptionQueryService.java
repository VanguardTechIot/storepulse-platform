package com.vanguardtech.storepulse.platform.monitoring.domain.services;

import com.vanguardtech.storepulse.platform.monitoring.domain.model.aggregates.Consumption;
import com.vanguardtech.storepulse.platform.monitoring.domain.model.queries.GetConsumptionByResourceIdQuery;
import com.vanguardtech.storepulse.platform.monitoring.domain.model.queries.GetConsumptionByResourceIdsQuery;

import java.util.List;

public interface ConsumptionQueryService {
    List<Consumption> handle(GetConsumptionByResourceIdQuery query);

    List<Consumption> handle(GetConsumptionByResourceIdsQuery query);
}