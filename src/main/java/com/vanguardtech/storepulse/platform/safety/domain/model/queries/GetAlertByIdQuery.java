package com.vanguardtech.storepulse.platform.safety.domain.model.queries;

public record GetAlertByIdQuery(Long alertId) {
    public GetAlertByIdQuery {
        if (alertId == null || alertId <= 0)
            throw new IllegalArgumentException("Alert ID must be a positive number");
    }
}
