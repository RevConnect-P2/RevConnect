package com.revconnect.controller;

import com.revconnect.entity.Follow;
import com.revconnect.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/follow")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    // =========================
    // FOLLOW USER
    // =========================
    @PostMapping("/{followerId}/{followingId}")
    public String followUser(@PathVariable Long followerId,
                             @PathVariable Long followingId) {

        followService.followUser(followerId, followingId);

        return "User followed successfully";
    }

    // =========================
    // UNFOLLOW USER
    // =========================
    @DeleteMapping("/{followerId}/{followingId}")
    public String unfollowUser(@PathVariable Long followerId,
                               @PathVariable Long followingId) {

        followService.unfollowUser(followerId, followingId);

        return "User unfollowed successfully";
    }

    // =========================
    // TOGGLE FOLLOW
    // =========================
    @PostMapping("/toggle/{followerId}/{followingId}")
    public boolean toggleFollow(@PathVariable Long followerId,
                                @PathVariable Long followingId) {

        return followService.toggleFollow(followerId, followingId);
    }

    // =========================
    // GET FOLLOWERS COUNT
    // =========================
    @GetMapping("/followers/count/{userId}")
    public long getFollowersCount(@PathVariable Long userId) {

        return followService.getFollowersCount(userId);
    }

    // =========================
    // GET FOLLOWING COUNT
    // =========================
    @GetMapping("/following/count/{userId}")
    public long getFollowingCount(@PathVariable Long userId) {

        return followService.getFollowingCount(userId);
    }

    // =========================
    // GET FOLLOWERS LIST
    // =========================
    @GetMapping("/followers/{userId}")
    public List<Follow> getFollowers(@PathVariable Long userId) {

        return followService.getFollowers(userId);
    }

    // =========================
    // GET FOLLOWING LIST
    // =========================
    @GetMapping("/following/{userId}")
    public List<Follow> getFollowing(@PathVariable Long userId) {

        return followService.getFollowing(userId);
    }
}