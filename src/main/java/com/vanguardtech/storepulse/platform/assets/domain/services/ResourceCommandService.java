package com.vanguardtech.storepulse.platform.assets.domain.services;

import com.vanguardtech.storepulse.platform.assets.domain.model.commands.SeedResourcesCommand;

public interface ResourceCommandService {

    void handle(SeedResourcesCommand command);
}
