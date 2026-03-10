package com.revconnect.service.impl;

import com.revconnect.entity.Connection;
import com.revconnect.entity.User;
import com.revconnect.enums.ConnectionStatus;
import com.revconnect.enums.NotificationType;
import com.revconnect.repository.ConnectionRepository;
import com.revconnect.repository.UserRepository;
import com.revconnect.service.NotificationService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ConnectionServiceImplTest {

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

    @Before
    public void setup() {

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

    // ---------------- SEND REQUEST ----------------

    @Test(expected = RuntimeException.class)
    public void shouldThrowIfConnectingYourself() {
        connectionService.sendConnectionRequest(1L,1L);
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowIfSenderNotFound() {

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        connectionService.sendConnectionRequest(1L,2L);
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowIfReceiverNotFound() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        connectionService.sendConnectionRequest(1L,2L);
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowIfConnectionAlreadyExists() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));

        when(connectionRepository.findBySenderAndReceiver(sender,receiver))
                .thenReturn(Optional.of(connection));

        connectionService.sendConnectionRequest(1L,2L);
    }

    @Test
    public void shouldSendConnectionRequestSuccessfully() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));

        when(connectionRepository.findBySenderAndReceiver(sender,receiver))
                .thenReturn(Optional.empty());

        when(connectionRepository.save(any(Connection.class)))
                .thenReturn(connection);

        connectionService.sendConnectionRequest(1L,2L);

        verify(connectionRepository).save(any(Connection.class));

        verify(notificationService).createNotification(
                1L,
                2L,
                10L,
                NotificationType.CONNECTION,
                null
        );
    }

    // ---------------- ACCEPT REQUEST ----------------

    @Test(expected = RuntimeException.class)
    public void shouldThrowIfConnectionNotFoundWhileAccept() {

        when(connectionRepository.findById(10L))
                .thenReturn(Optional.empty());

        connectionService.acceptRequest(10L);
    }

    @Test
    public void shouldAcceptConnectionSuccessfully() {

        when(connectionRepository.findById(10L))
                .thenReturn(Optional.of(connection));

        connectionService.acceptRequest(10L);

        verify(connectionRepository).save(connection);

        verify(notificationService).createNotification(
                receiver.getUserId(),
                sender.getUserId(),
                10L,
                NotificationType.CONNECTION,
                "accepted your connection request"
        );
    }

    // ---------------- REJECT REQUEST ----------------

    @Test(expected = RuntimeException.class)
    public void shouldThrowIfConnectionNotFoundWhileReject() {

        when(connectionRepository.findById(10L))
                .thenReturn(Optional.empty());

        connectionService.rejectRequest(10L);
    }

    @Test
    public void shouldRejectConnectionSuccessfully() {

        when(connectionRepository.findById(10L))
                .thenReturn(Optional.of(connection));

        connectionService.rejectRequest(10L);

        verify(connectionRepository).save(connection);
    }

    // ---------------- REMOVE CONNECTION ----------------

    @Test
    public void shouldRemoveConnectionSuccessfully() {

        connectionService.removeConnection(10L);

        verify(connectionRepository).deleteById(10L);

    }
}