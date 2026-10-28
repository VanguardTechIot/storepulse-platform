package com.vanguardtech.storepulse.platform.dashboardanalytics.domain.model.aggregates;

import com.vanguardtech.storepulse.platform.dashboardanalytics.domain.model.valueobjects.NotificationType;
import com.vanguardtech.storepulse.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Notification generated from a critical event of another Bounded Context. The device and the
 * resource (stand or common area) where it happened are external references.
 */
@Entity
@Table(name = "notifications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends AuditableAbstractAggregateRoot<Notification> {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(nullable = false)
    private Long deviceId;

    private Long resourceId;

    @Column(nullable = false)
    private String message;

    @Column(name = "is_read")
    private boolean read;

    public Notification(NotificationType type, Long deviceId, Long resourceId, String message) {
        this.type = type;
        this.deviceId = deviceId;
        this.resourceId = resourceId;
        this.message = message;
    }

    public void markAsRead() {
        read = true;
    }
}