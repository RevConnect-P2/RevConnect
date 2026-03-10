package com.revconnect.entity;

import com.revconnect.enums.ConnectionStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConnectionTest {

    @Test
    void shouldSetAndGetFieldsCorrectly() {

        User sender = new User();
        sender.setUserId(1L);

        User receiver = new User();
        receiver.setUserId(2L);

        Connection connection = new Connection();

        connection.setConnectionId(100L);
        connection.setSender(sender);
        connection.setReceiver(receiver);
        connection.setStatus(ConnectionStatus.PENDING);

        assertEquals(100L, connection.getConnectionId());
        assertEquals(sender, connection.getSender());
        assertEquals(receiver, connection.getReceiver());
        assertEquals(ConnectionStatus.PENDING, connection.getStatus());
    }

    @Test
    void shouldBuildConnectionUsingBuilder() {

        User sender = new User();
        sender.setUserId(1L);

        User receiver = new User();
        receiver.setUserId(2L);

        Connection connection = Connection.builder()
                .connectionId(5L)
                .sender(sender)
                .receiver(receiver)
                .status(ConnectionStatus.PENDING)
                .build();

        assertEquals(5L, connection.getConnectionId());
        assertEquals(sender, connection.getSender());
        assertEquals(receiver, connection.getReceiver());
    }

    @Test
    void shouldSetCreatedAtAndDefaultStatusOnPrePersist() {

        User sender = new User();
        sender.setUserId(1L);

        User receiver = new User();
        receiver.setUserId(2L);

        Connection connection = new Connection();
        connection.setSender(sender);
        connection.setReceiver(receiver);

        connection.onCreate();

        assertNotNull(connection.getCreatedAt());
        assertEquals(ConnectionStatus.PENDING, connection.getStatus());
    }

    @Test
    void shouldSetUpdatedAtOnPreUpdate() {

        User sender = new User();
        sender.setUserId(1L);

        User receiver = new User();
        receiver.setUserId(2L);

        Connection connection = new Connection();
        connection.setSender(sender);
        connection.setReceiver(receiver);
        connection.setStatus(ConnectionStatus.PENDING);

        connection.onUpdate();

        assertNotNull(connection.getUpdatedAt());
    }

    @Test
    void shouldThrowExceptionWhenUserConnectsToSelf() {

        User user = new User();
        user.setUserId(1L);

        Connection connection = new Connection();
        connection.setSender(user);
        connection.setReceiver(user);

        assertThrows(
                IllegalStateException.class,
                connection::onCreate
        );
    }
}