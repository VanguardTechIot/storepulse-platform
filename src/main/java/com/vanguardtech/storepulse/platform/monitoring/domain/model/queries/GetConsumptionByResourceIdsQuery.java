package com.vanguardtech.storepulse.platform.monitoring.domain.model.queries;

import java.util.Collection;

public record GetConsumptionByResourceIdsQuery(Collection<Long> resourceIds) {
    public GetConsumptionByResourceIdsQuery {
        if (resourceIds == null)
            throw new IllegalArgumentException("Resource IDs are required");
    }
}