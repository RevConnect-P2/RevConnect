package com.revconnect.service;

import com.revconnect.entity.Follow;

import java.util.List;

public interface FollowService {

    // =========================
    // FOLLOW USER
    // =========================
    void followUser(Long followerId, Long followingId);

    // =========================
    // UNFOLLOW USER
    // =========================
    void unfollowUser(Long followerId, Long followingId);

    // =========================
    // TOGGLE FOLLOW / UNFOLLOW
    // =========================
    boolean toggleFollow(Long followerId, Long followingId);

    // =========================
    // GET FOLLOWERS COUNT
    // =========================
    long getFollowersCount(Long userId);

    // =========================
    // GET FOLLOWING COUNT
    // =========================
    long getFollowingCount(Long userId);

    // =========================
    // GET FOLLOWERS LIST
    // =========================
    List<Follow> getFollowers(Long userId);

    // =========================
    // GET FOLLOWING LIST
    // =========================
    List<Follow> getFollowing(Long userId);
}