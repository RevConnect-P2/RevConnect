package com.revconnect.service;

import com.revconnect.entity.Connection;
import com.revconnect.enums.ConnectionStatus;

import java.util.List;

public interface ConnectionService {

    // =========================
    // Send connection request
    // =========================
    void sendConnectionRequest(Long senderId, Long receiverId);

    // =========================
    // Accept connection request
    // =========================
    void acceptRequest(Long connectionId);

    // =========================
    // Reject connection request
    // =========================
    void rejectRequest(Long connectionId);

    // =========================
    // Remove connection
    // =========================
    void removeConnection(Long connectionId);

    // =========================
    // Get received requests
    // =========================
    List<Connection> getReceivedRequests(Long userId);

    // =========================
    // Get sent requests
    // =========================
    List<Connection> getSentRequests(Long userId);

    // =========================
    // Count user connections
    // =========================
    long getConnectionsCount(Long userId);

    // =========================
    // Get connection status
    // =========================
    ConnectionStatus getConnectionStatus(Long user1, Long user2);

}