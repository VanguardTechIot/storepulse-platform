package com.vanguardtech.storepulse.platform.dashboardanalytics.domain.model.valueobjects;

import java.util.Map;

/** Alerts of Safety grouped by attention status, plus the open ones grouped by sensor type. */
public record AlertsSummary(long active, long acknowledged, long resolved, Map<String, Long> openBySensorType) {

    public AlertsSummary {
        openBySensorType = Map.copyOf(openBySensorType);
    }

    public long open() {
        return active + acknowledged;
    }
}