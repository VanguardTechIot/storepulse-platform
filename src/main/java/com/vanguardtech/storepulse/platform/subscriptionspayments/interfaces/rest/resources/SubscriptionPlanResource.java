package com.vanguardtech.storepulse.platform.subscriptionspayments.interfaces.rest.resources;

import java.math.BigDecimal;
import java.util.List;

public record SubscriptionPlanResource(Long id, String code, Integer monitoredUnitLimit, BigDecimal monthlyPrice,
                                       BigDecimal annualPrice, String currency, List<String> features,
                                       boolean published) {
}