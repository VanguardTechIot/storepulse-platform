package com.vanguardtech.storepulse.platform.monitoring.application.internal.queryservices;

import com.vanguardtech.storepulse.platform.monitoring.domain.model.aggregates.Consumption;
import com.vanguardtech.storepulse.platform.monitoring.domain.model.queries.GetAllConsumptionQuery;
import com.vanguardtech.storepulse.platform.monitoring.domain.model.queries.GetConsumptionByResourceIdQuery;
import com.vanguardtech.storepulse.platform.monitoring.domain.model.queries.GetConsumptionByResourceIdsQuery;
import com.vanguardtech.storepulse.platform.monitoring.domain.services.ConsumptionQueryService;
import com.vanguardtech.storepulse.platform.monitoring.infrastructure.persistence.jpa.repositories.ConsumptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ConsumptionQueryServiceImpl implements ConsumptionQueryService {

    private final ConsumptionRepository consumptionRepository;

    public ConsumptionQueryServiceImpl(ConsumptionRepository consumptionRepository) {
        this.consumptionRepository = consumptionRepository;
    }

    @Override
    public List<Consumption> handle(GetAllConsumptionQuery query) {
        return consumptionRepository.findAllByOrderByPeriodStartAsc();
    }

    @Override
    public List<Consumption> handle(GetConsumptionByResourceIdQuery query) {
        return consumptionRepository.findByResourceIdOrderByPeriodStartAsc(query.resourceId());
    }

    @Override
    public List<Consumption> handle(GetConsumptionByResourceIdsQuery query) {
        if (query.resourceIds().isEmpty()) return List.of();
        return consumptionRepository.findByResourceIdInOrderByPeriodStartAsc(query.resourceIds());
    }
}