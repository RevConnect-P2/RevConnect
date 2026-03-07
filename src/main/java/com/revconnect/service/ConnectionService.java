package com.revconnect.service;

import com.revconnect.entity.Connection;
import com.revconnect.entity.User;

import java.util.List;

public interface ConnectionService {

    Connection sendRequest(User sender, User receiver);

    Connection acceptRequest(Long connectionId, User currentUser);

    Connection rejectRequest(Long connectionId, User currentUser);

    List<Connection> getPendingRequests(User currentUser);

    List<Connection> getConnections(User currentUser);

    void removeConnection(Long connectionId, User currentUser);
    List<Connection> getPendingSentRequests(User user);

}