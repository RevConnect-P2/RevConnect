package com.revconnect.mapper;

import com.revconnect.dto.response.NotificationResponse;
import com.revconnect.entity.Notification;
import com.revconnect.enums.NotificationType;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class NotificationMapperTest {

    private NotificationMapper mapper;

    @Before
    public void setup() {
        mapper = new NotificationMapper();
    }

    @Test
    public void shouldMapNotificationToResponse() {

        Notification notification = new Notification();
        notification.setId(1L);
        notification.setMessage("New like on your post");
        notification.setType(NotificationType.LIKE);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        NotificationResponse response = mapper.toResponse(notification);

        assertNotNull(response);
        assertEquals(Long.valueOf(1L), response.getId());
        assertEquals("New like on your post", response.getMessage());
        assertEquals(NotificationType.LIKE, response.getType());
        assertFalse(response.isRead());
        assertNotNull(response.getCreatedAt());
    }
}