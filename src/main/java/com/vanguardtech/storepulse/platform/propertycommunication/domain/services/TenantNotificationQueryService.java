package com.vanguardtech.storepulse.platform.propertycommunication.domain.services;

import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.aggregates.TenantNotification;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.queries.GetNotificationByIdQuery;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.queries.GetNotificationsByAdministratorIdQuery;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.queries.GetNotificationsByTenantIdQuery;
import java.util.List;
import java.util.Optional;

public interface TenantNotificationQueryService {
    List<TenantNotification> handle(GetNotificationsByAdministratorIdQuery query);
    List<TenantNotification> handle(GetNotificationsByTenantIdQuery query);
    Optional<TenantNotification> handle(GetNotificationByIdQuery query);
}
