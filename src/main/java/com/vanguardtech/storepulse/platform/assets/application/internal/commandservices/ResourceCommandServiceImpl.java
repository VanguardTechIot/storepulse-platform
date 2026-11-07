package com.vanguardtech.storepulse.platform.assets.application.internal.commandservices;


import com.vanguardtech.storepulse.platform.assets.domain.model.aggregates.Resource;
import com.vanguardtech.storepulse.platform.assets.domain.model.commands.SeedResourcesCommand;
import com.vanguardtech.storepulse.platform.assets.domain.model.valueobjects.Location;
import com.vanguardtech.storepulse.platform.assets.domain.services.ResourceCommandService;
import com.vanguardtech.storepulse.platform.assets.infrastructure.persistence.jpa.repositories.ResourceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

@Service
public class ResourceCommandServiceImpl implements ResourceCommandService {

    private static final String GALLERY_ADDRESS = "Jr. de la Unión 845, Cercado de Lima";

    private final ResourceRepository resourceRepository;

    public ResourceCommandServiceImpl(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    @Override
    @Transactional
    public void handle(SeedResourcesCommand command) {
        Stream.of(
                        stand("Stand A-03", "1", "Pasillo A"),
                        stand("Stand A-05", "1", "Pasillo A"),
                        stand("Stand A-07", "1", "Pasillo A"),
                        stand("Stand B-07", "1", "Pasillo B"),
                        stand("Stand B-09", "1", "Pasillo B"),
                        stand("Stand B-12", "1", "Pasillo B"),
                        stand("Stand C-04", "2", "Pasillo C"),
                        stand("Stand C-07", "2", "Pasillo C"),
                        commonArea("Main hall", "1", "Main entrance"),
                        commonArea("Second floor hall", "2", "Escalator landing"))

                .filter(resource -> !resourceRepository.existsByName(resource.getName()))
                .forEach(resourceRepository::save);
    }

    private Resource commonArea(String name, String floor, String reference) {
        return new Resource(name, "Common area", new Location(GALLERY_ADDRESS, floor, reference));
    }

    private Resource stand(String name, String floor, String reference) {
        return new Resource(name, "Commercial stand", new Location(GALLERY_ADDRESS, floor, reference));
    }


}
