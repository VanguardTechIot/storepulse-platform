package com.vanguardtech.storepulse.platform.utilitybilling.interfaces.rest.transform;

import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.commands.GenerateUtilityBillCommand;
import com.vanguardtech.storepulse.platform.utilitybilling.interfaces.rest.resources.GenerateUtilityBillResource;

public class GenerateUtilityBillCommandFromResourceAssembler {

    public static GenerateUtilityBillCommand toCommandFromResource(GenerateUtilityBillResource resource) {
        return new GenerateUtilityBillCommand(resource.unitId(), resource.unitCode(), resource.unitName(),
                resource.tenantName(), resource.period());
    }
}