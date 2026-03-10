package com.revconnect.mapper;

import com.revconnect.dto.response.NotificationResponse;
import com.revconnect.entity.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationResponse toResponse(Notification notification){

        NotificationResponse response = new NotificationResponse();

        response.setId(notification.getId());
        response.setMessage(notification.getMessage());
        response.setType(notification.getType());
        response.setRead(notification.isRead());
        response.setCreatedAt(notification.getCreatedAt());

        return response;
    }

}