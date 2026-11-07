package com.vanguardtech.storepulse.platform.utilitybilling.interfaces.rest.transform;

import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.aggregates.UtilityBill;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.entities.BillingItem;
import com.vanguardtech.storepulse.platform.utilitybilling.interfaces.rest.resources.BillingItemResource;
import com.vanguardtech.storepulse.platform.utilitybilling.interfaces.rest.resources.UtilityBillResource;

public class UtilityBillResourceFromEntityAssembler {

    public static UtilityBillResource toResourceFromEntity(UtilityBill bill) {
        return new UtilityBillResource(bill.getId(), bill.getCode(), bill.getUnitId(), bill.getUnitCode(),
                bill.getUnitName(), bill.getTenantName(), bill.getBillingPeriod().startDate(),
                bill.getBillingPeriod().endDate(), bill.getStatus().name(),
                bill.getBreakdown().stream().map(UtilityBillResourceFromEntityAssembler::toItemResource).toList(),
                bill.getTotalAmount(), bill.getDataCompleteness(), bill.isDataWarningAccepted(), bill.getIssuedAt(),
                bill.getDueDate());
    }

    private static BillingItemResource toItemResource(BillingItem item) {
        return new BillingItemResource(item.getUtilityType().name(), item.getConsumptionValue(), item.getBaseline(),
                item.getUnit(), item.getRate(), item.getAmount());
    }
}