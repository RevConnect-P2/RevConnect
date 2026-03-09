package com.revconnect.entity;

import com.revconnect.enums.NotificationType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotificationTest {

    @Test
    void shouldSetAndGetFieldsCorrectly() {

        User sender = new User();
        sender.setUserId(1L);

        User receiver = new User();
        receiver.setUserId(2L);

        Notification notification = new Notification();

        notification.setId(100L);
        notification.setSender(sender);
        notification.setReceiver(receiver);
        notification.setReferenceId(50L);
        notification.setType(NotificationType.LIKE);
        notification.setMessage("User liked your post");
        notification.setRead(true);

        assertEquals(100L, notification.getId());
        assertEquals(sender, notification.getSender());
        assertEquals(receiver, notification.getReceiver());
        assertEquals(50L, notification.getReferenceId());
        assertEquals(NotificationType.LIKE, notification.getType());
        assertEquals("User liked your post", notification.getMessage());
        assertTrue(notification.isRead());
    }

    @Test
    void shouldDefaultReadFalse() {

        Notification notification = new Notification();

        assertFalse(notification.isRead());
    }

    @Test
    void shouldSetCreatedAtOnPrePersist() {

        Notification notification = new Notification();

        notification.onCreate();

        assertNotNull(notification.getCreatedAt());
    }

    @Test
    void shouldSetUpdatedAtOnPreUpdate() {

        Notification notification = new Notification();

        notification.onUpdate();

        assertNotNull(notification.getUpdatedAt());
    }
}