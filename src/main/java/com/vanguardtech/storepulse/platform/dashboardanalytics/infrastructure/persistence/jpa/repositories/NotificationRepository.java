package com.vanguardtech.storepulse.platform.dashboardanalytics.infrastructure.persistence.jpa.repositories;

import com.vanguardtech.storepulse.platform.dashboardanalytics.domain.model.aggregates.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findAllByOrderByCreatedAtDescIdDesc();

    @Transactional
    @Modifying
    @Query("update Notification n set n.read = true where n.read = false")
    void markAllAsRead();
}