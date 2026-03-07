package com.revconnect.service;

public interface ConnectionService {

    // Send connection request
    void sendConnectionRequest(Long senderId, Long receiverId);

    // Accept connection request
    void acceptRequest(Long connectionId);

    // Reject connection request
    void rejectRequest(Long connectionId);

    // Remove connection
    void removeConnection(Long connectionId);

}