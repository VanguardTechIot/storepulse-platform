package com.vanguardtech.storepulse.platform.dashboardanalytics.domain.services;

import com.vanguardtech.storepulse.platform.dashboardanalytics.domain.model.aggregates.Notification;
import com.vanguardtech.storepulse.platform.dashboardanalytics.domain.model.queries.GetAllNotificationsQuery;

import java.util.List;

public interface NotificationQueryService {

    /** Most recent first. */
    List<Notification> handle(GetAllNotificationsQuery query);
}