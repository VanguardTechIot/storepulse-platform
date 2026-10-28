package com.vanguardtech.storepulse.platform.monitoring.domain.model.queries;

public record GetConsumptionByResourceIdQuery(Long resourceId) {
    public GetConsumptionByResourceIdQuery {
        if (resourceId == null || resourceId <= 0)
            throw new IllegalArgumentException("Resource ID must be a positive number");
    }
}
