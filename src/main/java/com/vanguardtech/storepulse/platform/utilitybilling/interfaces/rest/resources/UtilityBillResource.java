package com.vanguardtech.storepulse.platform.utilitybilling.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record UtilityBillResource(Long id, String code, Long unitId, String unitCode, String unitName,
                                  String tenantName, LocalDate periodStart, LocalDate periodEnd, String status,
                                  List<BillingItemResource> items, BigDecimal totalAmount, int dataCompleteness,
                                  boolean dataWarningAccepted, Instant issuedAt, LocalDate dueDate) {
}