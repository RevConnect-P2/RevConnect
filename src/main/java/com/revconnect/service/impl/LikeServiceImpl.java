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

// LOGGER IMPORTS
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private static final Logger logger =
            LogManager.getLogger(LikeServiceImpl.class);

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostLikeRepository postLikeRepository;
    private final NotificationService notificationService;

    // =====================================
    // LIKE POST
    // =====================================
    @Override
    public void likePost(Long postId, String email) {

        logger.info("User {} attempting to like post {}", email, postId);

        User user = getUserByEmail(email);
        Post post = getPostById(postId);

        Optional<PostLike> existingLike =
                postLikeRepository.findByPost_PostIdAndUser_Email(postId, email);

        if (existingLike.isPresent()) {
            logger.warn("User {} already liked post {}", email, postId);
            throw new RuntimeException("You already liked this post");
        }

        PostLike like = PostLike.builder()
                .post(post)
                .user(user)
                .build();

        postLikeRepository.save(like);

        logger.info("User {} liked post {}", email, postId);

        createLikeNotification(user, post);
    }

    // =====================================
    // UNLIKE POST
    // =====================================
    @Override
    public void unlikePost(Long postId, String email) {

        logger.info("User {} attempting to unlike post {}", email, postId);

        PostLike like = postLikeRepository
                .findByPost_PostIdAndUser_Email(postId, email)
                .orElseThrow(() -> {
                    logger.error("Like not found for user {} on post {}", email, postId);
                    return new RuntimeException("Like not found");
                });

        postLikeRepository.delete(like);

        logger.info("User {} unliked post {}", email, postId);
    }

    // =====================================
    // TOGGLE LIKE
    // =====================================
    @Override
    public boolean toggleLike(Long postId, String email) {

        logger.info("User {} toggling like for post {}", email, postId);

        User user = getUserByEmail(email);
        Post post = getPostById(postId);

        Optional<PostLike> existingLike =
                postLikeRepository.findByPost_PostIdAndUser_Email(postId, email);

        // If already liked → remove like
        if (existingLike.isPresent()) {

            postLikeRepository.delete(existingLike.get());

            logger.info("User {} removed like from post {}", email, postId);

            return false;
        }

        // If not liked → add like
        PostLike like = PostLike.builder()
                .user(user)
                .post(post)
                .build();

        postLikeRepository.save(like);

        logger.info("User {} liked post {} via toggle", email, postId);

        createLikeNotification(user, post);

        return true;
    }

    // =====================================
    // USERS WHO LIKED
    // =====================================
    @Override
    public List<String> getUsersWhoLiked(Long postId) {

        logger.info("Fetching users who liked post {}", postId);

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

        logger.debug("Fetching user by email {}", email);

        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("User not found with email {}", email);
                    return new RuntimeException("User not found");
                });
    }

    private Post getPostById(Long postId) {

        logger.debug("Fetching post {}", postId);

        return postRepository.findById(postId)
                .orElseThrow(() -> {
                    logger.error("Post not found {}", postId);
                    return new RuntimeException("Post not found");
                });
    }

    private void createLikeNotification(User sender, Post post) {

        Long senderId = sender.getUserId();
        Long receiverId = post.getUser().getUserId();

        // prevent self notification
        if (!senderId.equals(receiverId)) {

            logger.debug("Creating like notification from {} to {}", senderId, receiverId);

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