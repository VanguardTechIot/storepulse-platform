package com.vanguardtech.storepulse.platform.assets.interfaces.rest.transform;

import com.vanguardtech.storepulse.platform.assets.domain.model.aggregates.Resource;
import com.vanguardtech.storepulse.platform.assets.interfaces.rest.resources.ResourceResource;

public class ResourceResourceFromEntityAssembler {

    public static ResourceResource toResourceFromEntity(Resource resource){
        var location = resource.getLocation();
        return new ResourceResource(
                resource.getId(),
                resource.getName(),
                resource.getDescription(),
                location.address(),
                location.floor(),
                location.reference(),
                resource.getStatus().name()
        );
    }
}
