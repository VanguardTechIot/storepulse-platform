package com.vanguardtech.storepulse.platform.safety.domain.model.valueobjects;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;

public final class BusinessHours {

    private static final ZoneId ZONE = ZoneId.of("America/Lima");
    private static final LocalTime OPENING = LocalTime.of(10, 0);
    private static final LocalTime CLOSING = LocalTime.of(22, 0);

    private BusinessHours() {
    }

    public static boolean includes(Instant instant) {
        var time = instant.atZone(ZONE).toLocalTime();
        return !time.isBefore(OPENING) && time.isBefore(CLOSING);
    }
}
