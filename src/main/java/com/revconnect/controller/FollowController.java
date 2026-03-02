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

    // Use IDs instead of full User objects
    @PostMapping("/add")
    public Follow follow(@RequestParam Long followerId,
                         @RequestParam Long followingId) {
        return followService.followUser(followerId, followingId);
    }

    @DeleteMapping("/unfollow")
    public void unfollow(@RequestParam Long followerId,
                         @RequestParam Long followingId) {
        followService.unfollowUser(followerId, followingId);
    }

    @GetMapping("/followers/{userId}")
    public List<Follow> getFollowers(@PathVariable Long userId) {
        return followService.getFollowers(userId);
    }

    @GetMapping("/following/{userId}")
    public List<Follow> getFollowing(@PathVariable Long userId) {
        return followService.getFollowing(userId);
    }
}