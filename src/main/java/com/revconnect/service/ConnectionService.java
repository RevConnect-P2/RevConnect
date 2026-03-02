package com.revconnect.service;

import com.revconnect.entity.Connection;

import java.util.List;

public interface ConnectionService {

    Connection sendConnectionRequest(Long senderId, Long receiverId);

    Connection acceptRequest(Long connectionId);

    Connection rejectRequest(Long connectionId);

    void removeConnection(Long connectionId);

    List<Connection> getPendingRequests(Long userId);

    List<Connection> getUserConnections(Long userId);
}