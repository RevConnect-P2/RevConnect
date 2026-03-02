package com.revconnect.service.impl;

import com.revconnect.entity.Post;
import com.revconnect.entity.User;
import com.revconnect.enums.NotificationType;
import com.revconnect.service.CommentService;
import com.revconnect.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final NotificationService notificationService;

    @Override
    public void addComment(Post post, User currentUser) {

        // Save comment logic here

        if (!post.getUser().getUserId()
                .equals(currentUser.getUserId())) {

            notificationService.createNotification(
                    post.getUser().getUserId(),
                    currentUser.getUserId(),
                    post.getPostId(),
                    NotificationType.COMMENT,
                    currentUser.getUsername() + " commented on your post"
            );
        }
    }
}