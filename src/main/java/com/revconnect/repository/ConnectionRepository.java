package com.revconnect.repository;

import com.revconnect.entity.Connection;
import com.revconnect.entity.User;
import com.revconnect.enums.ConnectionStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface ConnectionRepository extends JpaRepository<Connection, Long> {

    // =====================================
    // Check connection both directions
    // =====================================
    Optional<Connection> findBySenderAndReceiverOrReceiverAndSender(
            User sender,
            User receiver,
            User receiver2,
            User sender2
    );

    // =====================================
    // Sent connection requests
    // =====================================
    List<Connection> findBySender_UserId(Long userId);

    // =====================================
    // ACCEPTED connections (sender side)
    // =====================================
    List<Connection> findBySender_UserIdAndStatus(
            Long userId,
            ConnectionStatus status
    );

    // =====================================
    // ACCEPTED connections (receiver side)
    // =====================================
    List<Connection> findByReceiver_UserIdAndStatus(
            Long userId,
            ConnectionStatus status
    );

    // =====================================
    // ALL ACCEPTED CONNECTIONS (BOTH SIDES)
    // =====================================
    @Query("""
           SELECT c
           FROM Connection c
           WHERE (c.sender.userId = :userId OR c.receiver.userId = :userId)
           AND c.status = 'ACCEPTED'
           """)
    List<Connection> findAllAcceptedConnections(@Param("userId") Long userId);

    // =====================================
    // Correct count query
    // =====================================
    @Query("""
           SELECT COUNT(c)
           FROM Connection c
           WHERE (c.sender.userId = :userId OR c.receiver.userId = :userId)
           AND c.status = :status
           """)
    long countAcceptedConnections(
            @Param("userId") Long userId,
            @Param("status") ConnectionStatus status
    );
}