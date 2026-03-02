package com.revconnect.service;

import com.revconnect.entity.Follow;
import com.revconnect.entity.User;

import java.util.List;

public interface FollowService {
    Follow followUser(Long followerId, Long followingId);
    void unfollowUser(Long followerId, Long followingId);
    List<Follow> getFollowers(Long userId);
    List<Follow> getFollowing(Long userId);
}