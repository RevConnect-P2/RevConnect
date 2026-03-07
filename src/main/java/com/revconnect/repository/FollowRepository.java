package com.revconnect.repository;

import com.revconnect.entity.Follow;
import com.revconnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    // Find follow relationship
    Optional<Follow> findByFollowerAndFollowing(User follower, User following);

    // Get followers of a user
    List<Follow> findByFollowing_UserId(Long userId);

    // Get users someone is following
    List<Follow> findByFollower_UserId(Long userId);

}