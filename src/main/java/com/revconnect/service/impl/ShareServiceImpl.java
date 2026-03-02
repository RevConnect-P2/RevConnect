package com.revconnect.service.impl;

import com.revconnect.entity.Post;
import com.revconnect.entity.User;
import com.revconnect.enums.NotificationType;
import com.revconnect.service.NotificationService;
import com.revconnect.service.ShareService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShareServiceImpl implements ShareService {

    private final NotificationService notificationService;

    @Override
    public void sharePost(Post post, User currentUser) {

        // Save share logic

        notificationService.createNotification(
                post.getUser().getUserId(),
                currentUser.getUserId(),
                post.getPostId(),
                NotificationType.SHARE,
                currentUser.getUsername() + " shared your post"
        );
    }
}