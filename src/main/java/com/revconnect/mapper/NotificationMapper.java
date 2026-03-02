package com.revconnect.mapper;

import com.revconnect.dto.response.NotificationResponse;
import com.revconnect.entity.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationResponse toResponse(Notification notification) {

        if (notification == null) return null;

        return NotificationResponse.builder()
                .notificationId(notification.getNotificationId())
                .message(notification.getMessage())
                .type(notification.getType())                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .referenceId(notification.getReferenceId())
                .userId(notification.getUser().getUserId())
                .build();
    }
}