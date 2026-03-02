package com.revconnect.dto.response;

import com.revconnect.enums.NotificationType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificationResponse {

    private Long notificationId;
    private NotificationType type;
    private Long userId;
    private String message;
    private Long referenceId;
    private Long senderId;
    private String senderName;
    private Boolean isRead;
    private LocalDateTime createdAt;
}