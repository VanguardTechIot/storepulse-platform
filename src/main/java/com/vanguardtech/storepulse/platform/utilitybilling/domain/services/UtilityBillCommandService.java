package com.vanguardtech.storepulse.platform.utilitybilling.domain.services;

import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.aggregates.UtilityBill;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.commands.GenerateUtilityBillCommand;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.commands.IssueUtilityBillCommand;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.commands.VerifyBillingDataCommand;

public interface UtilityBillCommandService {

    UtilityBill handle(GenerateUtilityBillCommand command);

    UtilityBill handle(VerifyBillingDataCommand command);

    UtilityBill handle(IssueUtilityBillCommand command);
}