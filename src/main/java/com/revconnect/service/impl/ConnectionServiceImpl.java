package com.revconnect.service.impl;

import com.revconnect.entity.Connection;
import com.revconnect.entity.User;
import com.revconnect.repository.ConnectionRepository;
import com.revconnect.repository.UserRepository;
import com.revconnect.service.ConnectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConnectionServiceImpl implements ConnectionService {

    private final ConnectionRepository connectionRepository;
    private final UserRepository userRepository;

    @Override
    public Connection sendConnectionRequest(Long senderId, Long receiverId) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        Connection connection = Connection.builder()
                .sender(sender)
                .receiver(receiver)
                .status("PENDING")
                .build();

        return connectionRepository.save(connection);
    }

    @Override
    public Connection acceptRequest(Long connectionId) {
        Connection connection = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new RuntimeException("Connection not found"));
        connection.setStatus("ACCEPTED");
        return connectionRepository.save(connection);
    }

    @Override
    public Connection rejectRequest(Long connectionId) {
        Connection connection = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new RuntimeException("Connection not found"));
        connection.setStatus("REJECTED");
        return connectionRepository.save(connection);
    }

    @Override
    public void removeConnection(Long connectionId) {
        connectionRepository.deleteById(connectionId);
    }

    @Override
    public List<Connection> getPendingRequests(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return connectionRepository.findByReceiverAndStatus(user, "PENDING");
    }

    @Override
    public List<Connection> getUserConnections(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return connectionRepository.findBySenderOrReceiverAndStatus(user, user, "ACCEPTED");
    }
}