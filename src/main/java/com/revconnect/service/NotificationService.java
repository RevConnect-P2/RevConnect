package com.revconnect.service;

import com.revconnect.entity.Notification;
import com.revconnect.enums.NotificationType;
import org.springframework.data.domain.Page;

public interface NotificationService {

    Page<Notification> getAll(Long userId, int page, int size);

    Page<Notification> getByType(Long userId,
                                 NotificationType type,
                                 int page,
                                 int size);

    String markAsRead(Long id, Long userId);

    String markAsUnread(Long id, Long userId);

    String markAllAsRead(Long userId);

    long getUnreadCount(Long userId);

    void delete(Long id, Long userId);

    void createNotification(Long receiverId,
                            Long senderId,
                            Long referenceId,
                            NotificationType type,
                            String message);


    void notifyFollowersOfNewPost(Long authorId, Long postId);
    long getUnreadCountByType(Long userId, NotificationType type);
}