package com.revconnect.repository;

import com.revconnect.entity.Connection;
import com.revconnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConnectionRepository extends JpaRepository<Connection, Long> {

    Optional<Connection> findBySenderAndReceiver(User sender, User receiver);

    List<Connection> findByReceiverAndStatus(User receiver, String status);

    List<Connection> findBySenderOrReceiverAndStatus(User sender, User receiver, String status);

    @Query("SELECT c FROM Connection c WHERE (c.sender = :user OR c.receiver = :user) AND c.status = 'ACCEPTED'")
    List<Connection> findAllAcceptedConnections(@Param("user") User user);

    List<Connection> findBySenderAndStatus(User user, String pending);
}