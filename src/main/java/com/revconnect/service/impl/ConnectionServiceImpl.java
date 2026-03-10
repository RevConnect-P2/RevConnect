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

// ✅ LOGGER IMPORTS
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
@RequiredArgsConstructor
public class ConnectionServiceImpl implements ConnectionService {

    // ✅ LOGGER OBJECT
    private static final Logger logger =
            LogManager.getLogger(ConnectionServiceImpl.class);

    private final ConnectionRepository connectionRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;


    // =========================
    // SEND CONNECTION REQUEST
    // =========================
    @Override
    public void sendConnectionRequest(Long senderId, Long receiverId) {

        logger.info("Connection request attempt from {} to {}", senderId, receiverId);

        if (senderId.equals(receiverId)) {

            logger.error("User {} attempted to connect with themselves", senderId);

            throw new RuntimeException("You cannot connect with yourself");
        }

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> {

                    logger.error("Sender not found with ID {}", senderId);

                    return new RuntimeException("Sender not found");
                });

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> {

                    logger.error("Receiver not found with ID {}", receiverId);

                    return new RuntimeException("Receiver not found");
                });

        Optional<Connection> existing =
                connectionRepository.findConnectionBetweenUsers(sender, receiver);

        if (existing.isPresent()) {

            Connection connection = existing.get();

            if (connection.getStatus() == ConnectionStatus.PENDING) {

                logger.warn("Connection request already pending between {} and {}", senderId, receiverId);

                throw new RuntimeException("Connection request already sent");
            }

            if (connection.getStatus() == ConnectionStatus.ACCEPTED) {

                logger.warn("Users {} and {} are already connected", senderId, receiverId);

                throw new RuntimeException("You are already connected");
            }

            if (connection.getStatus() == ConnectionStatus.REJECTED) {

                logger.info("Resending previously rejected connection request from {} to {}", senderId, receiverId);

                connection.setStatus(ConnectionStatus.PENDING);

                Connection saved = connectionRepository.save(connection);

                notificationService.createNotification(
                        senderId,
                        receiverId,
                        saved.getConnectionId(),
                        NotificationType.CONNECTION,
                        "sent you a connection request"
                );

                logger.info("Connection request resent successfully");

                return;
            }
        }

        Connection connection = Connection.builder()
                .sender(sender)
                .receiver(receiver)
                .status(ConnectionStatus.PENDING)
                .build();

        Connection saved = connectionRepository.save(connection);

        logger.info("Connection request saved with ID {}", saved.getConnectionId());

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

        logger.info("Accepting connection request {}", connectionId);

        Connection connection = connectionRepository.findById(connectionId)
                .orElseThrow(() -> {

                    logger.error("Connection not found with ID {}", connectionId);

                    return new RuntimeException("Connection not found");
                });

        connection.setStatus(ConnectionStatus.ACCEPTED);

        Connection savedConnection = connectionRepository.save(connection);

        logger.info("Connection {} accepted", connectionId);

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

        logger.info("Rejecting connection request {}", connectionId);

        Connection connection = connectionRepository.findById(connectionId)
                .orElseThrow(() -> {

                    logger.error("Connection not found with ID {}", connectionId);

                    return new RuntimeException("Connection not found");
                });

        connection.setStatus(ConnectionStatus.REJECTED);

        Connection savedConnection = connectionRepository.save(connection);

        logger.info("Connection {} rejected", connectionId);

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

        logger.info("Removing connection {}", connectionId);

        connectionRepository.deleteById(connectionId);

        logger.info("Connection {} removed successfully", connectionId);
    }


    // =========================
    // GET RECEIVED REQUESTS
    // =========================
    @Override
    public List<Connection> getReceivedRequests(Long userId) {

        logger.info("Fetching received connection requests for user {}", userId);

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

        logger.info("Fetching sent connection requests for user {}", userId);

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

        logger.info("Counting accepted connections for user {}", userId);

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

        logger.info("Checking connection status between {} and {}", user1, user2);

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

        logger.info("Fetching connections list for user {}", userId);

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