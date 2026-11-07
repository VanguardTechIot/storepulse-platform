package com.vanguardtech.storepulse.platform.safety.interfaces.rest.transform;

import com.vanguardtech.storepulse.platform.safety.domain.model.commands.ResolveAlertCommand;
import com.vanguardtech.storepulse.platform.safety.interfaces.rest.resources.ResolveAlertResource;

public class ResolveAlertCommandFromResourceAssembler {

    public static ResolveAlertCommand toCommandFromResource(Long alertId, ResolveAlertResource resource) {
        return new ResolveAlertCommand(alertId, resource.resolutionNote());
    }
}
