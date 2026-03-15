package com.revconnect.service;

import com.revconnect.dto.response.NotificationResponse;
import com.revconnect.enums.NotificationType;

import java.util.List;

public interface NotificationService {

    // CREATE NOTIFICATION
    void createNotification(
            Long senderId,
            Long receiverId,
            Long referenceId,
            NotificationType type,
            String extraText
    );

    // GET ALL NOTIFICATIONS
    List<NotificationResponse> getUserNotifications(Long userId);

    // UNREAD COUNT
    long getUnreadCount(Long userId);

    // MARK AS READ
    void markAsRead(Long notificationId);

    // MARK AS UNREAD
    void markAsUnread(Long notificationId);

    // MARK ALL AS READ
    void markAllAsRead(Long userId);

    // DELETE NOTIFICATION
    void deleteNotification(Long notificationId);

    // FILTER BY TYPE
    List<NotificationResponse> getNotificationsByType(Long userId, NotificationType type);

    // ENABLE NOTIFICATIONS
    void enableNotifications(Long userId);

    // DISABLE NOTIFICATIONS
    void disableNotifications(Long userId);

    // CHECK IF ENABLED
    boolean isNotificationEnabled(Long userId);


    // UNREAD COUNT BY TYPE
    long getUnreadCountByType(Long userId, NotificationType type);
}