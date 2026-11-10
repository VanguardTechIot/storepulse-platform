package com.vanguardtech.storepulse.platform.propertycommunication.domain.model.aggregates;

import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.valueobjects.CommunicationAction;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.valueobjects.NotificationChannel;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.valueobjects.ReferenceType;
import com.vanguardtech.storepulse.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;
import java.time.Instant;

/**
 * Record of a communication with a tenant, kept for traceability and audit (Aggregate Root). It
 * never changes once registered.
 */
@Entity
@Table(name = "communication_logs")
@Getter
public class CommunicationLog extends AuditableAbstractAggregateRoot<CommunicationLog> {

    @Column(nullable = false)
    private Long referenceId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReferenceType referenceType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CommunicationAction action;

    @Column(nullable = false)
    private Long tenantId;

    @Column(nullable = false)
    private Long galleryAdministratorId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationChannel channel;

    /** Subject of the conversation or title of the notification. */
    @Column(nullable = false, length = 200)
    private String detail;

    @Column(nullable = false)
    private Instant loggedAt;

    protected CommunicationLog() {
    }

    public CommunicationLog(Long referenceId, ReferenceType referenceType, CommunicationAction action, Long tenantId,
                            Long galleryAdministratorId, NotificationChannel channel, String detail) {
        if (referenceId == null || referenceType == null || action == null || channel == null)
            throw new IllegalArgumentException("Reference, action and channel are required");
        if (tenantId == null || galleryAdministratorId == null)
            throw new IllegalArgumentException("Tenant and administrator are required");
        this.referenceId = referenceId;
        this.referenceType = referenceType;
        this.action = action;
        this.tenantId = tenantId;
        this.galleryAdministratorId = galleryAdministratorId;
        this.channel = channel;
        this.detail = detail == null ? "" : detail.trim();
        this.loggedAt = Instant.now();
    }
}
