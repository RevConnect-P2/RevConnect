package com.revconnect.service.impl;

import com.revconnect.enums.NotificationType;
import com.revconnect.service.ConnectionService;
import com.revconnect.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConnectionServiceImpl implements ConnectionService {

    private final NotificationService notificationService;

    @Override
    public void sendConnectionRequest(Long senderId, Long receiverId) {

        notificationService.createNotification(
                senderId,
                receiverId,
                receiverId,
                NotificationType.CONNECTION,
                null
        );
    }
}