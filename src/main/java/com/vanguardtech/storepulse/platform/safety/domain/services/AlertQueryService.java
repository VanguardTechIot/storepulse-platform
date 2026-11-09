package com.vanguardtech.storepulse.platform.safety.domain.services;

import com.vanguardtech.storepulse.platform.safety.domain.model.aggregates.Alert;
import com.vanguardtech.storepulse.platform.safety.domain.model.queries.GetAlertByIdQuery;
import com.vanguardtech.storepulse.platform.safety.domain.model.queries.GetAlertsByDeviceIdsQuery;
import com.vanguardtech.storepulse.platform.safety.domain.model.queries.GetAllAlertsQuery;

import java.util.List;
import java.util.Optional;

public interface AlertQueryService {
    List<Alert> handle(GetAllAlertsQuery query);

    List<Alert> handle(GetAlertsByDeviceIdsQuery query);

    Optional<Alert> handle(GetAlertByIdQuery query);
}