package com.vanguardtech.storepulse.platform.propertycommunication.domain.model.aggregates;

import com.vanguardtech.storepulse.platform.propertycommunication.domain.exceptions.PropertyCommunicationError;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.valueobjects.NotificationChannel;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.valueobjects.NotificationStatus;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.valueobjects.NotificationType;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import static org.junit.jupiter.api.Assertions.*;

class TenantNotificationTest {

    private static TenantNotification notification() {
        return new TenantNotification(5L, 3L, 7L, NotificationType.ANNOUNCEMENT, " Corte de agua ", "El sábado no habrá agua.",
                NotificationChannel.IN_APP, null);
    }

    private static PropertyCommunicationError errorOf(Executable action) {
        var exception = assertThrows(BusinessRuleException.class, action);
        return PropertyCommunicationError.valueOf(exception.getMessage().toUpperCase());
    }

    @Test
    void isCreatedPendingAndUnread() {
        var notification = notification();

        assertEquals("Corte de agua", notification.getTitle());
        assertEquals(NotificationStatus.PENDING, notification.getStatus());
        assertFalse(notification.isRead());
    }

    @Test
    void rejectsAnInvalidTitleOrBody() {
        assertEquals(PropertyCommunicationError.INVALID_NOTIFICATION_TITLE, errorOf(() -> new TenantNotification(5L, 3L, 7L,
                NotificationType.ANNOUNCEMENT, " ", "body", NotificationChannel.IN_APP, null)));
        assertEquals(PropertyCommunicationError.INVALID_NOTIFICATION_BODY, errorOf(() -> new TenantNotification(5L, 3L, 7L,
                NotificationType.ANNOUNCEMENT, "title", "x".repeat(1001), NotificationChannel.IN_APP, null)));
    }

    @Test
    void retriesOnlyFailedDeliveries() {
        var notification = notification();
        assertEquals(PropertyCommunicationError.NOTIFICATION_NOT_FAILED, errorOf(notification::retry));

        notification.markFailed();
        notification.retry();
        assertEquals(NotificationStatus.PENDING, notification.getStatus());

        notification.markSent();
        notification.markRead();
        assertEquals(NotificationStatus.SENT, notification.getStatus());
        assertTrue(notification.isRead());
    }
}
