package com.revconnect.service.impl;

import com.revconnect.entity.Follow;
import com.revconnect.entity.User;
import com.revconnect.enums.NotificationType;
import com.revconnect.repository.FollowRepository;
import com.revconnect.repository.UserRepository;
import com.revconnect.service.FollowService;
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
public class FollowServiceImpl implements FollowService {

    private static final Logger logger =
            LogManager.getLogger(FollowServiceImpl.class);

    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;


    // FOLLOW USER

    @Override
    public void followUser(Long followerId, Long followingId) {

        logger.info("User {} attempting to follow user {}", followerId, followingId);

        validateSelfFollow(followerId, followingId);

        User follower = getUser(followerId);
        User following = getUser(followingId);

        Optional<Follow> existingFollow =
                followRepository.findByFollowerAndFollowing(follower, following);

        if (existingFollow.isPresent()) {

            logger.warn("User {} already follows user {}", followerId, followingId);

            throw new RuntimeException("You are already following this user");
        }

        Follow follow = Follow.builder()
                .follower(follower)
                .following(following)
                .build();

        followRepository.save(follow);

        logger.info("User {} successfully followed user {}", followerId, followingId);

        sendFollowNotification(followerId, followingId);
    }


    // UNFOLLOW USER

    @Override
    public void unfollowUser(Long followerId, Long followingId) {

        logger.info("User {} attempting to unfollow user {}", followerId, followingId);

        validateSelfFollow(followerId, followingId);

        User follower = getUser(followerId);
        User following = getUser(followingId);

        Follow follow = followRepository
                .findByFollowerAndFollowing(follower, following)
                .orElseThrow(() -> {

                    logger.error("User {} is not following user {}", followerId, followingId);

                    return new RuntimeException("You are not following this user");
                });

        followRepository.delete(follow);

        logger.info("User {} unfollowed user {}", followerId, followingId);
    }


    // TOGGLE FOLLOW

    @Override
    public boolean toggleFollow(Long followerId, Long followingId) {

        logger.info("User {} toggling follow for user {}", followerId, followingId);

        validateSelfFollow(followerId, followingId);

        User follower = getUser(followerId);
        User following = getUser(followingId);

        Optional<Follow> existingFollow =
                followRepository.findByFollowerAndFollowing(follower, following);

        // If already following → unfollow
        if (existingFollow.isPresent()) {

            followRepository.delete(existingFollow.get());

            logger.info("User {} unfollowed user {} via toggle", followerId, followingId);

            return false;
        }

        // Follow user
        Follow follow = Follow.builder()
                .follower(follower)
                .following(following)
                .build();

        followRepository.save(follow);

        logger.info("User {} followed user {} via toggle", followerId, followingId);

        sendFollowNotification(followerId, followingId);

        return true;
    }


    // GET FOLLOWERS COUNT

    @Override
    public long getFollowersCount(Long userId) {

        logger.debug("Fetching followers count for user {}", userId);

        return followRepository.countByFollowing_UserId(userId);
    }


    // GET FOLLOWING COUNT

    @Override
    public long getFollowingCount(Long userId) {

        logger.debug("Fetching following count for user {}", userId);

        return followRepository.countByFollower_UserId(userId);
    }

    // =========================
    // GET FOLLOWERS LIST
    // =========================
    @Override
    public List<Follow> getFollowers(Long userId) {

        logger.info("Fetching followers list for user {}", userId);

        return followRepository.findByFollowing_UserId(userId);
    }


    // GET FOLLOWING LIST

    @Override
    public List<Follow> getFollowing(Long userId) {

        logger.info("Fetching following list for user {}", userId);

        return followRepository.findByFollower_UserId(userId);
    }


    // HELPER METHODS


    private User getUser(Long userId) {

        logger.debug("Fetching user {}", userId);

        return userRepository.findById(userId)
                .orElseThrow(() -> {

                    logger.error("User not found with id {}", userId);

                    return new RuntimeException("User not found with id: " + userId);
                });
    }

    private void validateSelfFollow(Long followerId, Long followingId) {

        if (followerId.equals(followingId)) {

            logger.warn("User {} attempted to follow themselves", followerId);

            throw new RuntimeException("You cannot follow yourself");
        }
    }

    private void sendFollowNotification(Long followerId, Long followingId) {

        logger.debug("Sending follow notification from {} to {}", followerId, followingId);

        notificationService.createNotification(
                followerId,
                followingId,
                null,
                NotificationType.FOLLOW,
                null
        );
    }

    @Override
    public boolean isFollowing(Long followerId, Long followingId) {

        logger.debug("Checking if user {} follows user {}", followerId, followingId);

        User follower = userRepository.findById(followerId)
                .orElseThrow();

        User following = userRepository.findById(followingId)
                .orElseThrow();

        return followRepository
                .findByFollowerAndFollowing(follower, following)
                .isPresent();
    }
}