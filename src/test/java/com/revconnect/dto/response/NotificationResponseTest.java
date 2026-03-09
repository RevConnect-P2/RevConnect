package com.revconnect.dto.response;

import com.revconnect.enums.NotificationType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class NotificationResponseTest {

    @Test
    void testGettersAndSetters() {

        NotificationResponse response = new NotificationResponse();

        LocalDateTime time = LocalDateTime.now();

        response.setId(1L);
        response.setMessage("New notification");
        response.setType(NotificationType.LIKE);
        response.setRead(true);
        response.setCreatedAt(time);

        assertEquals(1L, response.getId());
        assertEquals("New notification", response.getMessage());
        assertEquals(NotificationType.LIKE, response.getType());
        assertTrue(response.isRead());
        assertEquals(time, response.getCreatedAt());
    }
}