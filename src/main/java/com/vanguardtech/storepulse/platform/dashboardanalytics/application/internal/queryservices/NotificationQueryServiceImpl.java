package com.vanguardtech.storepulse.platform.dashboardanalytics.application.internal.queryservices;

import com.vanguardtech.storepulse.platform.dashboardanalytics.domain.model.aggregates.Notification;
import com.vanguardtech.storepulse.platform.dashboardanalytics.domain.model.queries.GetAllNotificationsQuery;
import com.vanguardtech.storepulse.platform.dashboardanalytics.domain.services.NotificationQueryService;
import com.vanguardtech.storepulse.platform.dashboardanalytics.infrastructure.persistence.jpa.repositories.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationQueryServiceImpl implements NotificationQueryService {

    private final NotificationRepository notificationRepository;

    @Override
    public List<Notification> handle(GetAllNotificationsQuery query) {
        return notificationRepository.findAllByOrderByCreatedAtDescIdDesc();
    }
}