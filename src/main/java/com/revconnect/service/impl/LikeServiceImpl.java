package com.revconnect.service.impl;

import com.revconnect.entity.Post;
import com.revconnect.entity.PostLike;
import com.revconnect.entity.User;
import com.revconnect.enums.NotificationType;
import com.revconnect.repository.PostLikeRepository;
import com.revconnect.repository.PostRepository;
import com.revconnect.repository.UserRepository;
import com.revconnect.service.LikeService;
import com.revconnect.service.NotificationService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostLikeRepository postLikeRepository;
    private final NotificationService notificationService;

    // =====================================
    // LIKE POST
    // =====================================
    @Override
    public void likePost(Long postId, String email) {

        User user = getUserByEmail(email);
        Post post = getPostById(postId);

        Optional<PostLike> existingLike =
                postLikeRepository.findByPost_PostIdAndUser_Email(postId, email);

        if (existingLike.isPresent()) {
            throw new RuntimeException("You already liked this post");
        }

        PostLike like = PostLike.builder()
                .post(post)
                .user(user)
                .build();

        postLikeRepository.save(like);

        createLikeNotification(user, post);
    }

    // =====================================
    // UNLIKE POST
    // =====================================
    @Override
    public void unlikePost(Long postId, String email) {

        PostLike like = postLikeRepository
                .findByPost_PostIdAndUser_Email(postId, email)
                .orElseThrow(() -> new RuntimeException("Like not found"));

        postLikeRepository.delete(like);
    }

    // =====================================
    // TOGGLE LIKE
    // =====================================
    @Override
    public boolean toggleLike(Long postId, String email) {

        User user = getUserByEmail(email);
        Post post = getPostById(postId);

        Optional<PostLike> existingLike =
                postLikeRepository.findByPost_PostIdAndUser_Email(postId, email);

        // If already liked → remove like
        if (existingLike.isPresent()) {

            postLikeRepository.delete(existingLike.get());
            return false;
        }

        // If not liked → add like
        PostLike like = PostLike.builder()
                .user(user)
                .post(post)
                .build();

        postLikeRepository.save(like);

        createLikeNotification(user, post);

        return true;
    }

    // =====================================
    // USERS WHO LIKED
    // =====================================
    @Override
    public List<String> getUsersWhoLiked(Long postId) {

        return postLikeRepository
                .findByPost_PostId(postId)
                .stream()
                .map(like -> like.getUser().getUsername())
                .toList();
    }

    // =====================================
    // HELPER METHODS
    // =====================================

    private User getUserByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private Post getPostById(Long postId) {

        return postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
    }

    private void createLikeNotification(User sender, Post post) {

        Long senderId = sender.getUserId();
        Long receiverId = post.getUser().getUserId();

        // prevent self notification
        if (!senderId.equals(receiverId)) {

            notificationService.createNotification(
                    senderId,
                    receiverId,
                    post.getPostId(),
                    NotificationType.LIKE,
                    null
            );
        }
    }
}