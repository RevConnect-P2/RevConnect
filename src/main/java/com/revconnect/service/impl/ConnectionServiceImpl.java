package com.revconnect.service.impl;

import com.revconnect.entity.Connection;
import com.revconnect.entity.User;
import com.revconnect.enums.ConnectionStatus;
import com.revconnect.enums.NotificationType;
import com.revconnect.repository.ConnectionRepository;
import com.revconnect.repository.UserRepository;
import com.revconnect.service.ConnectionService;
import com.revconnect.service.NotificationService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConnectionServiceImpl implements ConnectionService {

    private final ConnectionRepository connectionRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;


    // =========================
    // SEND CONNECTION REQUEST
    // =========================
    @Override
    public void sendConnectionRequest(Long senderId, Long receiverId) {

        if (senderId.equals(receiverId)) {
            throw new RuntimeException("You cannot connect with yourself");
        }

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        Optional<Connection> existing =
                connectionRepository.findConnectionBetweenUsers(sender, receiver);

        if (existing.isPresent()) {

            Connection connection = existing.get();

            if (connection.getStatus() == ConnectionStatus.PENDING) {
                throw new RuntimeException("Connection request already sent");
            }

            if (connection.getStatus() == ConnectionStatus.ACCEPTED) {
                throw new RuntimeException("You are already connected");
            }

            if (connection.getStatus() == ConnectionStatus.REJECTED) {

                connection.setStatus(ConnectionStatus.PENDING);

                Connection saved = connectionRepository.save(connection);

                notificationService.createNotification(
                        senderId,
                        receiverId,
                        saved.getConnectionId(),
                        NotificationType.CONNECTION,
                        "sent you a connection request"
                );

                return;
            }
        }

        Connection connection = Connection.builder()
                .sender(sender)
                .receiver(receiver)
                .status(ConnectionStatus.PENDING)
                .build();

        Connection saved = connectionRepository.save(connection);

        notificationService.createNotification(
                senderId,
                receiverId,
                saved.getConnectionId(),
                NotificationType.CONNECTION,
                "sent you a connection request"
        );
    }


    // =========================
    // ACCEPT REQUEST
    // =========================
    @Override
    public void acceptRequest(Long connectionId) {

        Connection connection = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new RuntimeException("Connection not found"));

        connection.setStatus(ConnectionStatus.ACCEPTED);

        Connection savedConnection = connectionRepository.save(connection);

        notificationService.createNotification(
                savedConnection.getReceiver().getUserId(),
                savedConnection.getSender().getUserId(),
                savedConnection.getConnectionId(),
                NotificationType.CONNECTION_ACCEPTED,
                null
        );
    }


    // =========================
    // REJECT REQUEST
    // =========================
    @Override
    public void rejectRequest(Long connectionId) {

        Connection connection = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new RuntimeException("Connection not found"));

        connection.setStatus(ConnectionStatus.REJECTED);

        Connection savedConnection = connectionRepository.save(connection);

        notificationService.createNotification(
                savedConnection.getReceiver().getUserId(),
                savedConnection.getSender().getUserId(),
                savedConnection.getConnectionId(),
                NotificationType.CONNECTION_REJECTED,
                null
        );
    }


    // =========================
    // REMOVE CONNECTION
    // =========================
    @Override
    public void removeConnection(Long connectionId) {

        connectionRepository.deleteById(connectionId);
    }


    // =========================
    // GET RECEIVED REQUESTS
    // =========================
    @Override
    public List<Connection> getReceivedRequests(Long userId) {

        return connectionRepository.findByReceiver_UserIdAndStatus(
                userId,
                ConnectionStatus.PENDING
        );
    }


    // =========================
    // GET SENT REQUESTS
    // =========================
    @Override
    public List<Connection> getSentRequests(Long userId) {

        return connectionRepository.findBySender_UserIdAndStatus(
                userId,
                ConnectionStatus.PENDING
        );
    }


    // =========================
    // COUNT USER CONNECTIONS
    // =========================
    @Override
    public long getConnectionsCount(Long userId) {

        return connectionRepository.countAcceptedConnections(
                userId,
                ConnectionStatus.ACCEPTED
        );
    }


    // =========================
    // GET CONNECTION STATUS
    // =========================
    @Override
    public ConnectionStatus getConnectionStatus(Long user1, Long user2) {

        User userA = userRepository.findById(user1)
                .orElseThrow(() -> new RuntimeException("User not found"));

        User userB = userRepository.findById(user2)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<Connection> connection =
                connectionRepository.findConnectionBetweenUsers(userA, userB);

        return connection.map(Connection::getStatus).orElse(null);
    }


    // =========================
    // GET MY CONNECTIONS
    // =========================
    @Override
    public List<User> getMyConnections(Long userId) {

        List<Connection> connections =
                connectionRepository.findAllAcceptedConnections(userId);

        return connections.stream()
                .map(connection -> {
                    if (connection.getSender().getUserId().equals(userId)) {
                        return connection.getReceiver();
                    } else {
                        return connection.getSender();
                    }
                })
                .distinct()
                .toList();
    }
}