package com.vanguardtech.storepulse.platform.assets.application.internal.eventhandlers;

import com.vanguardtech.storepulse.platform.assets.domain.model.commands.SeedResourcesCommand;
import com.vanguardtech.storepulse.platform.assets.domain.services.ResourceCommandService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service("assetsApplicationReadyEventHandler")
public class ApplicationReadyEventHandler {

    private final ResourceCommandService resourceCommandService;

    public ApplicationReadyEventHandler(ResourceCommandService resourceCommandService) {
        this.resourceCommandService = resourceCommandService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void on(){
        resourceCommandService.handle(new SeedResourcesCommand());
    }
}
