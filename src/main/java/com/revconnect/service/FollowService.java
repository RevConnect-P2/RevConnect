package com.revconnect.service;

import com.revconnect.entity.Follow;
import com.revconnect.entity.User;

import java.util.List;

public interface FollowService {

    Follow follow(User follower, User following);

    void unfollow(User follower, User following);

    List<User> getFollowers(User user);

    List<User> getFollowing(User user);
}