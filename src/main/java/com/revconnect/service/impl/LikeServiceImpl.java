package com.revconnect.service.impl;

import com.revconnect.entity.Post;
import com.revconnect.entity.User;
import com.revconnect.enums.NotificationType;
import com.revconnect.repository.PostLikeRepository;
import com.revconnect.service.LikeService;
import com.revconnect.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final PostLikeRepository postLikeRepository;
    private final NotificationService notificationService;

    @Override
    public void likePost(Post post, User currentUser) {

        // Save like logic here (your existing code)

        // Do NOT notify if user likes own post
        if (!post.getUser().getUserId()
                .equals(currentUser.getUserId())) {

            notificationService.createNotification(
                    post.getUser().getUserId(),     // receiver
                    currentUser.getUserId(),       // sender
                    post.getPostId(),              // reference
                    NotificationType.LIKE,
                    currentUser.getUsername() + " liked your post"
            );
        }
    }
}