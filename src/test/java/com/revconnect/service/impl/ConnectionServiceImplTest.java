package com.revconnect.service.impl;

import com.revconnect.entity.Connection;
import com.revconnect.entity.User;
import com.revconnect.enums.ConnectionStatus;
import com.revconnect.enums.NotificationType;
import com.revconnect.repository.ConnectionRepository;
import com.revconnect.repository.UserRepository;
import com.revconnect.service.NotificationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConnectionServiceImplTest {

    @Mock
    private ConnectionRepository connectionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ConnectionServiceImpl connectionService;

    private User sender;
    private User receiver;
    private Connection connection;

    @BeforeEach
    void setup() {

        sender = new User();
        sender.setUserId(1L);

        receiver = new User();
        receiver.setUserId(2L);

        connection = new Connection();
        connection.setConnectionId(10L);
        connection.setSender(sender);
        connection.setReceiver(receiver);
        connection.setStatus(ConnectionStatus.PENDING);
    }

    // =========================
    // SELF CONNECTION
    // =========================

    @Test
    void shouldThrowIfUserConnectsToSelf() {

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> connectionService.sendConnectionRequest(1L, 1L)
        );

        assertEquals("You cannot connect with yourself", ex.getMessage());
    }

    // =========================
    // SENDER NOT FOUND
    // =========================

    @Test
    void shouldThrowIfSenderNotFound() {

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> connectionService.sendConnectionRequest(1L, 2L));
    }

    // =========================
    // RECEIVER NOT FOUND
    // =========================

    @Test
    void shouldThrowIfReceiverNotFound() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> connectionService.sendConnectionRequest(1L, 2L));
    }

    // =========================
    // EXISTING PENDING
    // =========================

    @Test
    void shouldThrowIfPendingRequestExists() {

        connection.setStatus(ConnectionStatus.PENDING);

        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));

        when(connectionRepository.findConnectionBetweenUsers(sender, receiver))
                .thenReturn(Optional.of(connection));

        assertThrows(RuntimeException.class,
                () -> connectionService.sendConnectionRequest(1L, 2L));
    }

    // =========================
    // EXISTING ACCEPTED
    // =========================

    @Test
    void shouldThrowIfAlreadyConnected() {

        connection.setStatus(ConnectionStatus.ACCEPTED);

        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));

        when(connectionRepository.findConnectionBetweenUsers(sender, receiver))
                .thenReturn(Optional.of(connection));

        assertThrows(RuntimeException.class,
                () -> connectionService.sendConnectionRequest(1L, 2L));
    }

    // =========================
    // REJECTED → PENDING AGAIN
    // =========================

    @Test
    void shouldResendRequestIfPreviouslyRejected() {

        connection.setStatus(ConnectionStatus.REJECTED);

        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));

        when(connectionRepository.findConnectionBetweenUsers(sender, receiver))
                .thenReturn(Optional.of(connection));

        when(connectionRepository.save(connection)).thenReturn(connection);

        connectionService.sendConnectionRequest(1L, 2L);

        verify(connectionRepository).save(connection);
        verify(notificationService).createNotification(
                1L,
                2L,
                10L,
                NotificationType.CONNECTION,
                "sent you a connection request"
        );
    }

    // =========================
    // NEW CONNECTION
    // =========================

    @Test
    void shouldCreateNewConnection() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));

        when(connectionRepository.findConnectionBetweenUsers(sender, receiver))
                .thenReturn(Optional.empty());

        when(connectionRepository.save(any(Connection.class)))
                .thenReturn(connection);

        connectionService.sendConnectionRequest(1L, 2L);

        verify(connectionRepository).save(any(Connection.class));
        verify(notificationService).createNotification(
                1L,
                2L,
                10L,
                NotificationType.CONNECTION,
                "sent you a connection request"
        );
    }

    // =========================
    // ACCEPT REQUEST
    // =========================

    @Test
    void shouldAcceptRequest() {

        when(connectionRepository.findById(10L))
                .thenReturn(Optional.of(connection));

        when(connectionRepository.save(connection))
                .thenReturn(connection);   // IMPORTANT

        connectionService.acceptRequest(10L);

        assertEquals(ConnectionStatus.ACCEPTED, connection.getStatus());

        verify(connectionRepository).save(connection);
    }

    // =========================
    // REJECT REQUEST
    // =========================

    @Test
    void shouldRejectRequest() {

        when(connectionRepository.findById(10L))
                .thenReturn(Optional.of(connection));

        when(connectionRepository.save(connection))
                .thenReturn(connection);   // IMPORTANT

        connectionService.rejectRequest(10L);

        assertEquals(ConnectionStatus.REJECTED, connection.getStatus());

        verify(connectionRepository).save(connection);
    }
    // =========================
    // REMOVE CONNECTION
    // =========================

    @Test
    void shouldRemoveConnection() {

        connectionService.removeConnection(10L);

        verify(connectionRepository).deleteById(10L);
    }

    // =========================
    // GET CONNECTION COUNT
    // =========================

    @Test
    void shouldReturnConnectionCount() {

        when(connectionRepository.countAcceptedConnections(1L, ConnectionStatus.ACCEPTED))
                .thenReturn(5L);

        long count = connectionService.getConnectionsCount(1L);

        assertEquals(5L, count);
    }

    // =========================
    // GET MY CONNECTIONS
    // =========================

    @Test
    void shouldReturnMyConnections() {

        when(connectionRepository.findAllAcceptedConnections(1L))
                .thenReturn(List.of(connection));

        List<User> users = connectionService.getMyConnections(1L);

        assertEquals(1, users.size());
    }

    @Test
    void shouldReturnConnectionStatus() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));

        when(connectionRepository.findConnectionBetweenUsers(sender, receiver))
                .thenReturn(Optional.of(connection));

        ConnectionStatus status =
                connectionService.getConnectionStatus(1L, 2L);

        assertEquals(ConnectionStatus.PENDING, status);
    }

    @Test
    void shouldReturnNullIfConnectionDoesNotExist() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));

        when(connectionRepository.findConnectionBetweenUsers(sender, receiver))
                .thenReturn(Optional.empty());

        ConnectionStatus status =
                connectionService.getConnectionStatus(1L, 2L);

        assertNull(status);
    }

    @Test
    void shouldReturnReceivedRequests() {

        when(connectionRepository
                .findByReceiver_UserIdAndStatus(2L, ConnectionStatus.PENDING))
                .thenReturn(List.of(connection));

        List<Connection> list =
                connectionService.getReceivedRequests(2L);

        assertEquals(1, list.size());
    }

    @Test
    void shouldReturnSentRequests() {

        when(connectionRepository
                .findBySender_UserIdAndStatus(1L, ConnectionStatus.PENDING))
                .thenReturn(List.of(connection));

        List<Connection> list =
                connectionService.getSentRequests(1L);

        assertEquals(1, list.size());
    }




}