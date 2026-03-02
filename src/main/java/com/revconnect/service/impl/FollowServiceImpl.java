package com.revconnect.service.impl;

import com.revconnect.entity.User;
import com.revconnect.enums.NotificationType;
import com.revconnect.service.FollowService;
import com.revconnect.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {

    private final NotificationService notificationService;

    @Override
    public void followUser(User follower, User followedUser) {

        // Save follow logic here

        notificationService.createNotification(
                followedUser.getUserId(),        // receiver
                follower.getUserId(),            // sender
                follower.getUserId(),
                NotificationType.FOLLOW,
                follower.getUsername() + " started following you"
        );
    }
}