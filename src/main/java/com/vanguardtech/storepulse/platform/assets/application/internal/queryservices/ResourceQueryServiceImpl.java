package com.vanguardtech.storepulse.platform.assets.application.internal.queryservices;

import com.vanguardtech.storepulse.platform.assets.domain.model.aggregates.Resource;
import com.vanguardtech.storepulse.platform.assets.domain.model.queries.GetAllResourcesQuery;
import com.vanguardtech.storepulse.platform.assets.domain.services.ResourceQueryService;
import com.vanguardtech.storepulse.platform.assets.infrastructure.persistence.jpa.repositories.ResourceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResourceQueryServiceImpl implements ResourceQueryService {

   private final ResourceRepository resourceRepository;

   public ResourceQueryServiceImpl(ResourceRepository resourceRepository) {
      this.resourceRepository = resourceRepository;
   }

   @Override
   public List<Resource> handle(GetAllResourcesQuery query) {
        return resourceRepository.findAll();
   }


}
