package com.vanguardtech.storepulse.platform.monitoring.domain.model.aggregates;

import com.vanguardtech.storepulse.platform.monitoring.domain.model.valueobjects.MeasurementType;
import com.vanguardtech.storepulse.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneOffset;

@Entity
@Table(name = "consumption")
@Getter
public class Consumption extends AuditableAbstractAggregateRoot<Consumption> {

    @Column(nullable = false)
    private Long resourceId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MeasurementType measurementType;

    @Column(nullable = false)
    private Double totalValue;

    @Column(nullable = false)
    private Instant periodStart;

    @Column(nullable = false)
    private Instant periodEnd;

    protected Consumption() {
    }

    public Consumption(Long resourceId, MeasurementType measurementType, YearMonth period) {
        if (resourceId == null || resourceId <= 0)
            throw new IllegalArgumentException("Resource ID must be a positive number");
        if (measurementType == null || !measurementType.isConsumption())
            throw new IllegalArgumentException("Consumption is only recorded for electricity or water");
        if (period == null)
            throw new IllegalArgumentException("Consumption period is required");
        this.resourceId = resourceId;
        this.measurementType = measurementType;
        this.totalValue = 0.0;
        this.periodStart = startOf(period);
        this.periodEnd = startOf(period.plusMonths(1));
    }

    public void addReading(Double value) {
        if (value == null || value < 0)
            throw new IllegalArgumentException("Consumption reading cannot be negative");
        this.totalValue += value;
    }

    public static YearMonth periodOf(Instant instant) {
        return YearMonth.from(instant.atZone(ZoneOffset.UTC));
    }

    public static Instant startOf(YearMonth period) {
        return period.atDay(1).atStartOfDay().toInstant(ZoneOffset.UTC);
    }
}
