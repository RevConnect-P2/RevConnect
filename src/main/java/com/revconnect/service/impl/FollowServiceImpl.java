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

@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    // =========================
    // FOLLOW USER
    // =========================
    @Override
    public void followUser(Long followerId, Long followingId) {

        validateSelfFollow(followerId, followingId);

        User follower = getUser(followerId);
        User following = getUser(followingId);

        Optional<Follow> existingFollow =
                followRepository.findByFollowerAndFollowing(follower, following);

        if (existingFollow.isPresent()) {
            throw new RuntimeException("You are already following this user");
        }

        Follow follow = Follow.builder()
                .follower(follower)
                .following(following)
                .build();

        followRepository.save(follow);

        sendFollowNotification(followerId, followingId);
    }

    // =========================
    // UNFOLLOW USER
    // =========================
    @Override
    public void unfollowUser(Long followerId, Long followingId) {

        validateSelfFollow(followerId, followingId);

        User follower = getUser(followerId);
        User following = getUser(followingId);

        Follow follow = followRepository
                .findByFollowerAndFollowing(follower, following)
                .orElseThrow(() -> new RuntimeException("You are not following this user"));

        followRepository.delete(follow);
    }

    // =========================
    // TOGGLE FOLLOW
    // =========================
    @Override
    public boolean toggleFollow(Long followerId, Long followingId) {

        validateSelfFollow(followerId, followingId);

        User follower = getUser(followerId);
        User following = getUser(followingId);

        Optional<Follow> existingFollow =
                followRepository.findByFollowerAndFollowing(follower, following);

        // If already following → unfollow
        if (existingFollow.isPresent()) {

            followRepository.delete(existingFollow.get());
            return false;
        }

        // Follow user
        Follow follow = Follow.builder()
                .follower(follower)
                .following(following)
                .build();

        followRepository.save(follow);

        sendFollowNotification(followerId, followingId);

        return true;
    }

    // =========================
    // GET FOLLOWERS COUNT
    // =========================
    @Override
    public long getFollowersCount(Long userId) {

        return followRepository.countByFollowing_UserId(userId);
    }

    // =========================
    // GET FOLLOWING COUNT
    // =========================
    @Override
    public long getFollowingCount(Long userId) {

        return followRepository.countByFollower_UserId(userId);
    }

    // =========================
    // GET FOLLOWERS LIST
    // =========================
    @Override
    public List<Follow> getFollowers(Long userId) {

        return followRepository.findByFollowing_UserId(userId);
    }

    // =========================
    // GET FOLLOWING LIST
    // =========================
    @Override
    public List<Follow> getFollowing(Long userId) {

        return followRepository.findByFollower_UserId(userId);
    }

    // =========================
    // HELPER METHODS
    // =========================

    private User getUser(Long userId) {

        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
    }

    private void validateSelfFollow(Long followerId, Long followingId) {

        if (followerId.equals(followingId)) {
            throw new RuntimeException("You cannot follow yourself");
        }
    }

    private void sendFollowNotification(Long followerId, Long followingId) {

        notificationService.createNotification(
                followerId,
                followingId,
                null,
                NotificationType.FOLLOW,
                null
        );
    }
}