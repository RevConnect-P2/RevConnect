package com.revconnect.repository;

import com.revconnect.entity.Connection;
import com.revconnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConnectionRepository extends JpaRepository<Connection, Long> {

    Optional<Connection> findBySenderAndReceiver(User sender, User receiver);

    List<Connection> findByReceiverAndStatus(User receiver, String status);

    List<Connection> findBySenderAndStatus(User sender, String status);

    // Add this to fetch all accepted connections for a user
    List<Connection> findBySenderOrReceiverAndStatus(User sender, User receiver, String status);
}