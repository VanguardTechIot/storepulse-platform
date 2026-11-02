package com.vanguardtech.storepulse.platform.safety.application.internal.queryservices;

import com.vanguardtech.storepulse.platform.safety.domain.model.aggregates.Alert;
import com.vanguardtech.storepulse.platform.safety.domain.model.queries.GetAlertByIdQuery;
import com.vanguardtech.storepulse.platform.safety.domain.model.queries.GetAllAlertsQuery;
import com.vanguardtech.storepulse.platform.safety.domain.services.AlertQueryService;
import com.vanguardtech.storepulse.platform.safety.infrastructure.persistence.jpa.repositories.AlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AlertQueryServiceImpl implements AlertQueryService {

    private final AlertRepository alertRepository;

    @Override
    public List<Alert> handle(GetAllAlertsQuery query) {
        return alertRepository.findAllByOrderByDetectedAtDesc();
    }

    @Override
    public Optional<Alert> handle(GetAlertByIdQuery query) {
        return alertRepository.findById(query.alertId());
    }
}
