package com.revconnect.repository;

import com.revconnect.entity.Follow;
import com.revconnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    @Query("SELECT f.follower FROM Follow f WHERE f.following.userId = :userId")
    List<User> findFollowersByFollowingId(Long userId);
}