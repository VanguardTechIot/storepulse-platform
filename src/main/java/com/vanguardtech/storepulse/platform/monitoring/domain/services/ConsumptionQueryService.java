package com.vanguardtech.storepulse.platform.monitoring.domain.services;

import com.vanguardtech.storepulse.platform.monitoring.domain.model.aggregates.Consumption;
import com.vanguardtech.storepulse.platform.monitoring.domain.model.queries.GetAllConsumptionQuery;
import com.vanguardtech.storepulse.platform.monitoring.domain.model.queries.GetConsumptionByResourceIdQuery;

import java.util.List;

public interface ConsumptionQueryService {
    List<Consumption> handle(GetAllConsumptionQuery query);

    List<Consumption> handle(GetConsumptionByResourceIdQuery query);
}
