package com.revconnect.service.impl;

import com.revconnect.entity.Follow;
import com.revconnect.entity.User;
import com.revconnect.exception.BadRequestException;
import com.revconnect.repository.FollowRepository;
import com.revconnect.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FollowServiceImpl implements FollowService {

    private final FollowRepository followRepository;

    @Override
    public Follow follow(User follower, User following) {
        followRepository.findByFollowerAndFollowing(follower, following)
                .ifPresent(f -> { throw new BadRequestException("Already following"); });

        Follow follow = Follow.builder()
                .follower(follower)
                .following(following)
                .build();
        return followRepository.save(follow);
    }

    @Override
    public void unfollow(User follower, User following) {
        Follow follow = followRepository.findByFollowerAndFollowing(follower, following)
                .orElseThrow(() -> new BadRequestException("Not following"));

        followRepository.delete(follow);
    }

    @Override
    public List<User> getFollowers(User user) {
        return followRepository.findByFollowing(user)
                .stream()
                .map(Follow::getFollower)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getFollowing(User user) {
        return followRepository.findByFollower(user)
                .stream()
                .map(Follow::getFollowing)
                .collect(Collectors.toList());
    }
}