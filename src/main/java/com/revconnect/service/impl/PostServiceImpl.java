package com.revconnect.service.impl;

import com.revconnect.entity.Post;
import com.revconnect.entity.User;
import com.revconnect.repository.PostRepository;   // ✅ IMPORT
import com.revconnect.service.NotificationService;
import com.revconnect.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;          // ✅ ADD THIS
    private final NotificationService notificationService;

    @Override
    public void createPost(Post post, User author) {

        post.setUser(author);

        postRepository.save(post);   // ✅ Now this works

        notificationService.notifyFollowersOfNewPost(
                author.getUserId(),
                post.getPostId()
        );
    }
}