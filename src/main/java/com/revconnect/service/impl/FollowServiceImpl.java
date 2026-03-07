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

        if (followerId.equals(followingId)) {
            throw new RuntimeException("You cannot follow yourself");
        }

        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new RuntimeException("Follower not found"));

        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<Follow> existingFollow =
                followRepository.findByFollowerAndFollowing(follower, following);

        if (existingFollow.isPresent()) {
            throw new RuntimeException("Already following this user");
        }

        Follow follow = Follow.builder()
                .follower(follower)
                .following(following)
                .build();

        followRepository.save(follow);

        // 🔔 Send notification
        if (!followerId.equals(followingId)) {
            notificationService.createNotification(
                    followerId,
                    followingId,
                    null,
                    NotificationType.FOLLOW,
                    null
            );
        }
    }

    // =========================
    // UNFOLLOW USER
    // =========================
    @Override
    public void unfollowUser(Long followerId, Long followingId) {

        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new RuntimeException("Follower not found"));

        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Follow follow = followRepository
                .findByFollowerAndFollowing(follower, following)
                .orElseThrow(() -> new RuntimeException("Follow relationship not found"));

        followRepository.delete(follow);
    }

    // =========================
    // TOGGLE FOLLOW
    // =========================
    @Override
    public boolean toggleFollow(Long followerId, Long followingId) {

        if (followerId.equals(followingId)) {
            throw new RuntimeException("You cannot follow yourself");
        }

        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new RuntimeException("Follower not found"));

        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<Follow> existingFollow =
                followRepository.findByFollowerAndFollowing(follower, following);

        // Already following → unfollow
        if (existingFollow.isPresent()) {

            followRepository.delete(existingFollow.get());
            return false;
        }

        // Follow
        Follow follow = Follow.builder()
                .follower(follower)
                .following(following)
                .build();

        followRepository.save(follow);

        // 🔔 Notification
        notificationService.createNotification(
                followerId,
                followingId,
                null,
                NotificationType.FOLLOW,
                null
        );

        return true;
    }
}