package com.revconnect.service;

public interface FollowService {

    // follow user
    void followUser(Long followerId, Long followingId);

    // unfollow user
    void unfollowUser(Long followerId, Long followingId);

    // toggle follow / unfollow
    boolean toggleFollow(Long followerId, Long followingId);
}