package com.vanguardtech.storepulse.platform.utilitybilling.interfaces.rest.resources;

import java.time.Instant;

public record BillingDisputeResource(Long id, Long utilityBillId, Long tenantId, String reason, String description,
                                     String status, String reviewNotes, String resolution, Instant createdAt,
                                     Instant resolvedAt) {
}