package com.vanguardtech.storepulse.platform.propertymanagement.domain.services;

import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.aggregates.CommercialUnit;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.commands.ChangeUnitOccupancyCommand;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.commands.RegisterCommercialUnitCommand;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.commands.RemoveCommercialUnitCommand;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.commands.UpdateCommercialUnitCommand;

public interface CommercialUnitCommandService {
    CommercialUnit handle(RegisterCommercialUnitCommand command);
    CommercialUnit handle(UpdateCommercialUnitCommand command);
    CommercialUnit handle(RemoveCommercialUnitCommand command);
    CommercialUnit handle(ChangeUnitOccupancyCommand command);
}
