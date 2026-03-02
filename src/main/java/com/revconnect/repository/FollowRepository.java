package com.revconnect.repository;

import com.revconnect.entity.Follow;
import com.revconnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    List<Follow> findByFollower(User follower);  // Who I am following
    List<Follow> findByFollowing(User following);  // Who follows me

    Optional<Follow> findByFollowerAndFollowing(User follower, User following); // THIS
}