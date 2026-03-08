package com.revconnect.repository;

import com.revconnect.entity.Connection;
import com.revconnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface ConnectionRepository extends JpaRepository<Connection, Long> {

    // Find connection between two users
    Optional<Connection> findBySenderAndReceiver(User sender, User receiver);

    // Get received connection requests
    List<Connection> findByReceiver_UserId(Long userId);

    // Get sent connection requests
    List<Connection> findBySender_UserId(Long userId);

    // ⭐ COUNT TOTAL CONNECTIONS
    long countBySender_UserIdOrReceiver_UserId(Long senderId, Long receiverId);
}