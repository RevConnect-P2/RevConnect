package com.revconnect.repository;

import com.revconnect.entity.Follow;
import com.revconnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    // =========================
    // FIND FOLLOW RELATIONSHIP
    // =========================
    Optional<Follow> findByFollowerAndFollowing(User follower, User following);

    // =========================
    // GET FOLLOWERS OF A USER
    // =========================
    List<Follow> findByFollowing_UserId(Long userId);

    // =========================
    // GET USERS SOMEONE FOLLOWS
    // =========================
    List<Follow> findByFollower_UserId(Long userId);

    // =========================
    // COUNT FOLLOWERS
    // =========================
    long countByFollowing_UserId(Long userId);

    // =========================
    // COUNT FOLLOWING
    // =========================
    long countByFollower_UserId(Long userId);

}