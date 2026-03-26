package com.revconnect.controller;

import com.revconnect.entity.Follow;
import com.revconnect.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// ✅ LOGGER IMPORTS
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@RestController
@RequestMapping("/follow")
@RequiredArgsConstructor
public class FollowController {

    // LOGGER OBJECT
    private static final Logger logger =
            LogManager.getLogger(FollowController.class);

    private final FollowService followService;

    // =========================
    // FOLLOW USER
    // =========================
    @PostMapping("/{followerId}/{followingId}")
    public String followUser(@PathVariable Long followerId,
                             @PathVariable Long followingId) {

        logger.info("User {} attempting to follow user {}", followerId, followingId);

        followService.followUser(followerId, followingId);

        logger.info("User {} successfully followed user {}", followerId, followingId);

        return "User followed successfully";
    }

    // =========================
    // UNFOLLOW USER
    // =========================
    @DeleteMapping("/{followerId}/{followingId}")
    public String unfollowUser(@PathVariable Long followerId,
                               @PathVariable Long followingId) {

        logger.info("User {} attempting to unfollow user {}", followerId, followingId);

        followService.unfollowUser(followerId, followingId);

        logger.info("User {} successfully unfollowed user {}", followerId, followingId);

        return "User unfollowed successfully";
    }

    // =========================
    // TOGGLE FOLLOW
    // =========================
    @PostMapping("/toggle/{followerId}/{followingId}")
    public boolean toggleFollow(@PathVariable Long followerId,
                                @PathVariable Long followingId) {

        logger.info("Toggle follow request: follower {} -> following {}", followerId, followingId);

        boolean result = followService.toggleFollow(followerId, followingId);

        logger.info("Toggle follow result for {} -> {} : {}", followerId, followingId, result);

        return result;
    }

    // =========================
    // GET FOLLOWERS COUNT
    // =========================
    @GetMapping("/followers/count/{userId}")
    public long getFollowersCount(@PathVariable Long userId) {

        logger.info("Fetching followers count for user {}", userId);

        return followService.getFollowersCount(userId);
    }

    // =========================
    // GET FOLLOWING COUNT
    // =========================
    @GetMapping("/following/count/{userId}")
    public long getFollowingCount(@PathVariable Long userId) {

        logger.info("Fetching following count for user {}", userId);

        return followService.getFollowingCount(userId);
    }

    // =========================
    // GET FOLLOWERS LIST
    // =========================
    @GetMapping("/followers/{userId}")
    public List<Follow> getFollowers(@PathVariable Long userId) {

        logger.info("Fetching followers list for user {}", userId);

        return followService.getFollowers(userId);
    }

    // =========================
    // GET FOLLOWING LIST
    // =========================
    @GetMapping("/following/{userId}")
    public List<Follow> getFollowing(@PathVariable Long userId) {

        logger.info("Fetching following list for user {}", userId);

        return followService.getFollowing(userId);
    }
}