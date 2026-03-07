package com.revconnect.service.impl;

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
    public void followUser(Long followerId, Long followingId) {

        notificationService.createNotification(
                followerId,
                followingId,
                followingId,
                NotificationType.FOLLOW,
                null
        );
    }
}