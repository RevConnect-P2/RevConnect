package com.revconnect.service;

import com.revconnect.dto.response.NotificationResponse;
import com.revconnect.enums.NotificationType;

import java.util.List;

public interface NotificationService {

    void createNotification(Long senderId,
                            Long receiverId,
                            Long referenceId,
                            NotificationType type,
                            String extraText);

    List<NotificationResponse> getUserNotifications(Long userId);

    long getUnreadCount(Long userId);

    void markAsRead(Long id);

    void deleteNotification(Long id);

    void markAsUnread(Long id);

    void markAllAsRead(Long userId);

    List<NotificationResponse> getNotificationsByType(Long userId,
                                                      NotificationType type);

    long getUnreadCountByType(Long userId, NotificationType type);

}