package com.vanguardtech.storepulse.platform.assets.domain.model.queries;

import java.util.Collection;

public record GetIoTDevicesByResourceIdsQuery(Collection<Long> resourceIds) {
    public GetIoTDevicesByResourceIdsQuery {
        if (resourceIds == null)
            throw new IllegalArgumentException("Resource IDs are required.");
    }
}