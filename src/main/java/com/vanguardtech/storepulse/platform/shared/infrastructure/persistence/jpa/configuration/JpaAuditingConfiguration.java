package com.vanguardtech.storepulse.platform.shared.infrastructure.persistence.jpa.configuration;


import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Enables the {@code createdAt} / {@code updatedAt} audit fields declared by
 * {@code AuditableAbstractAggregateRoot}.
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfiguration {
}
