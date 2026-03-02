package com.revconnect.service.impl;

import com.revconnect.entity.Connection;
import com.revconnect.entity.User;
import com.revconnect.exception.BadRequestException;
import com.revconnect.repository.ConnectionRepository;
import com.revconnect.service.ConnectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ConnectionServiceImpl implements ConnectionService {

    private final ConnectionRepository connectionRepository;

//    @Override
//    public Connection sendRequest(User sender, User receiver) {
//        connectionRepository.findBySenderAndReceiver(sender, receiver)
//                .ifPresent(c -> { throw new BadRequestException("Request already exists"); });
//
//        Connection connection = Connection.builder()
//                .sender(sender)
//                .receiver(receiver)
//                .status("PENDING")
//                .build();
//        return connectionRepository.save(connection);
//    }

    @Override
    public Connection sendRequest(User sender, User receiver) {
        boolean exists = connectionRepository.findBySenderAndReceiver(sender, receiver).isPresent() ||
                connectionRepository.findBySenderAndReceiver(receiver, sender).isPresent();

        if (exists) throw new BadRequestException("Request already exists");

        Connection connection = Connection.builder()
                .sender(sender)
                .receiver(receiver)
                .status("PENDING")
                .build();
        return connectionRepository.save(connection);
    }

    @Override
    public Connection acceptRequest(Long connectionId, User currentUser) {
        Connection connection = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new BadRequestException("Connection not found"));

        if (!connection.getReceiver().getUserId().equals(currentUser.getUserId())) {
            throw new BadRequestException("Only receiver can accept");
        }

        connection.setStatus("ACCEPTED");
        return connectionRepository.save(connection);
    }

    @Override
    public Connection rejectRequest(Long connectionId, User currentUser) {
        Connection connection = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new BadRequestException("Connection not found"));

        if (!connection.getReceiver().getUserId().equals(currentUser.getUserId())) {
            throw new BadRequestException("Only receiver can reject");
        }

        connection.setStatus("REJECTED");
        return connectionRepository.save(connection);
    }

    @Override
    public List<Connection> getPendingRequests(User currentUser) {
        return connectionRepository.findByReceiverAndStatus(currentUser, "PENDING");
    }

//    @Override
//    public List<Connection> getConnections(User currentUser) {
//        return connectionRepository.findBySenderOrReceiverAndStatus(currentUser, currentUser, "ACCEPTED");
//    }

    @Override
    public List<Connection> getConnections(User currentUser) {
        return connectionRepository.findAllAcceptedConnections(currentUser);
    }

    @Override
    public void removeConnection(Long connectionId, User currentUser) {
        Connection connection = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new BadRequestException("Connection not found"));

        if (!connection.getSender().getUserId().equals(currentUser.getUserId()) &&
                !connection.getReceiver().getUserId().equals(currentUser.getUserId())) {
            throw new BadRequestException("Cannot remove connection of another user");
        }

        connectionRepository.delete(connection);
    }
    @Override
    public List<Connection> getPendingSentRequests(User user) {
        return connectionRepository.findBySenderAndStatus(user, "PENDING");
    }
}