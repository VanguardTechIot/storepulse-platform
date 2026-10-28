package com.vanguardtech.storepulse.platform.assets.domain.services;

import com.vanguardtech.storepulse.platform.assets.domain.model.aggregates.Resource;
import com.vanguardtech.storepulse.platform.assets.domain.model.queries.GetAllResourcesQuery;

import java.util.List;

public interface ResourceQueryService {
    List<Resource> handle(GetAllResourcesQuery query);
}
