package com.revconnect.service.impl;

import com.revconnect.entity.User;
import com.revconnect.enums.NotificationType;
import com.revconnect.repository.ConnectionRepository;
import com.revconnect.service.ConnectionService;
import com.revconnect.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConnectionServiceImpl implements ConnectionService {

    private final ConnectionRepository connectionRepository;
    private final NotificationService notificationService;

    @Override
    public void sendConnectionRequest(User sender, User receiver) {

        // save connection logic here

        notificationService.createNotification(
                receiver.getUserId(),
                sender.getUserId(),
                sender.getUserId(),
                NotificationType.CONNECTION,
                sender.getUsername() + " sent you a connection request"
        );
    }
}