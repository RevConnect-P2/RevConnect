package com.revconnect.repository;

import com.revconnect.entity.Connection;
import com.revconnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface ConnectionRepository extends JpaRepository<Connection, Long> {

    Optional<Connection> findBySenderAndReceiver(User sender, User receiver);

    List<Connection> findByReceiver_UserId(Long userId);

    List<Connection> findBySender_UserId(Long userId);

}