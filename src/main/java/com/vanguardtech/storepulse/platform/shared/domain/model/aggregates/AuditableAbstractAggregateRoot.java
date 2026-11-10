package com.vanguardtech.storepulse.platform.shared.domain.model.aggregates;


import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.AbstractAggregateRoot;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

/**
 * Base class of every Aggregate Root in the platform.
 *
 * <p>It provides the technical identifier, the audit timestamps and, through Spring Data's
 * {@link AbstractAggregateRoot}, the ability to register Domain Events with
 * {@code registerEvent(...)}: the events are published when the aggregate is saved.
 *
 * <p>It is the counterpart of {@code BaseEntity} in the web application's shared kernel.
 *
 * @param <T> the concrete aggregate root type
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public abstract class AuditableAbstractAggregateRoot<T extends AbstractAggregateRoot<T>>
        extends AbstractAggregateRoot<T> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
