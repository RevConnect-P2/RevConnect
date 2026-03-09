package com.revconnect.repository;

import com.revconnect.entity.Connection;
import com.revconnect.entity.User;
import com.revconnect.enums.ConnectionStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface ConnectionRepository extends JpaRepository<Connection, Long> {

    // =====================================
    // Find connection between two users
    // =====================================
    Optional<Connection> findBySenderAndReceiver(User sender, User receiver);

    // =====================================
    // Check connection both directions
    // (A → B or B → A)
    // =====================================
    Optional<Connection> findBySenderAndReceiverOrReceiverAndSender(
            User sender,
            User receiver,
            User receiver2,
            User sender2
    );

    // =====================================
    // Received connection requests
    // =====================================
    List<Connection> findByReceiver_UserId(Long userId);

    // =====================================
    // Received PENDING requests
    // =====================================
    List<Connection> findByReceiver_UserIdAndStatus(
            Long userId,
            ConnectionStatus status
    );

    // =====================================
    // Sent connection requests
    // =====================================
    List<Connection> findBySender_UserId(Long userId);

    // =====================================
    // All connections of a user
    // =====================================
    List<Connection> findBySender_UserIdOrReceiver_UserId(
            Long senderId,
            Long receiverId
    );

    // =====================================
    // Accepted connections only
    // =====================================
    List<Connection> findBySender_UserIdOrReceiver_UserIdAndStatus(
            Long senderId,
            Long receiverId,
            ConnectionStatus status
    );

    // =====================================
    // Count only ACCEPTED connections
    // =====================================
    long countBySender_UserIdOrReceiver_UserIdAndStatus(
            Long senderId,
            Long receiverId,
            ConnectionStatus status
    );

}