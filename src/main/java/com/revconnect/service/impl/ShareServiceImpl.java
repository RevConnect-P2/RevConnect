package com.revconnect.service.impl;

import com.revconnect.entity.Post;
import com.revconnect.entity.Share;
import com.revconnect.entity.User;
import com.revconnect.enums.NotificationType;
import com.revconnect.repository.PostRepository;
import com.revconnect.repository.ShareRepository;
import com.revconnect.repository.UserRepository;
import com.revconnect.service.AnalyticsService;
import com.revconnect.service.NotificationService;
import com.revconnect.service.ShareService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// LOGGER IMPORTS
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
@RequiredArgsConstructor
public class ShareServiceImpl implements ShareService {

    private static final Logger logger =
            LogManager.getLogger(ShareServiceImpl.class);

    private final ShareRepository shareRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final AnalyticsService analyticsService;

    // SHARE POST

    @Override
    public void sharePost(Long postId, String email) {

        logger.info("User {} attempting to share post {}", email, postId);

        User user = getUserByEmail(email);
        Post post = getPostById(postId);

        Optional<Share> existingShare =
                shareRepository.findByOriginalPost_PostIdAndSharedBy_UserId(
                        postId,
                        user.getUserId()
                );

        // already shared → do nothing
        if (existingShare.isPresent()) {

            logger.warn("User {} already shared post {}", email, postId);

            return;
        }

        Share share = Share.builder()
                .originalPost(post)
                .sharedBy(user)
                .build();

        shareRepository.save(share);
        analyticsService.incrementShares(post);

        logger.info("User {} shared post {}", email, postId);

        createShareNotification(user, post);
    }


    // UNSHARE POST

    @Override
    public void unsharePost(Long postId, String email) {

        logger.info("User {} attempting to unshare post {}", email, postId);

        User user = getUserByEmail(email);

        Share share = shareRepository
                .findByOriginalPost_PostIdAndSharedBy_UserId(
                        postId,
                        user.getUserId()
                )
                .orElseThrow(() -> {

                    logger.error("Share not found for user {} on post {}", email, postId);

                    return new RuntimeException("Share not found");
                });

        shareRepository.delete(share);
        analyticsService.decrementShares(share.getOriginalPost());

        logger.info("User {} unshared post {}", email, postId);
    }


    // TOGGLE SHARE

    @Override
    public boolean toggleShare(Long postId, String email) {

        logger.info("User {} toggling share for post {}", email, postId);

        User user = getUserByEmail(email);
        Post post = getPostById(postId);

        Optional<Share> existingShare =
                shareRepository.findByOriginalPost_PostIdAndSharedBy_UserId(
                        postId,
                        user.getUserId()
                );

        // already shared → unshare
        if (existingShare.isPresent()) {

            shareRepository.delete(existingShare.get());
            analyticsService.decrementShares(existingShare.get().getOriginalPost());

            logger.info("User {} unshared post {} via toggle", email, postId);

            return false;
        }

        // create share
        Share share = Share.builder()
                .originalPost(post)
                .sharedBy(user)
                .build();

        shareRepository.save(share);
        analyticsService.incrementShares(post);

        logger.info("User {} shared post {} via toggle", email, postId);

        createShareNotification(user, post);

        return true;
    }


    // SHARE COUNT

    @Override
    public Long getShareCount(Long postId) {

        logger.debug("Fetching share count for post {}", postId);

        return shareRepository.countByOriginalPost_PostId(postId);
    }


    // USERS WHO SHARED

    @Override
    public List<String> getUsersWhoShared(Long postId) {

        logger.info("Fetching users who shared post {}", postId);

        // optimized query from repository
        return shareRepository.findUsernamesWhoShared(postId);
    }


    // HELPER METHODS


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

                    logger.error("Post {} not found", postId);

                    return new RuntimeException("Post not found");
                });
    }

    // CREATE SHARE NOTIFICATION

    private void createShareNotification(User sender, Post post) {

        Long senderId = sender.getUserId();
        Long receiverId = post.getUser().getUserId();

        // prevent self notification
        if (!senderId.equals(receiverId)) {

            logger.debug("Sending share notification from {} to {}", senderId, receiverId);

            notificationService.createNotification(
                    senderId,
                    receiverId,
                    post.getPostId(),
                    NotificationType.SHARE,
                    null
            );
        }
    }
}