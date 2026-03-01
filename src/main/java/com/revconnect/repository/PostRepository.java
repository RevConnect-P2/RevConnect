package com.revconnect.repository;

import com.revconnect.entity.Post;
import com.revconnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    // Find pinned post of a user
    Optional<Post> findByUserAndPinnedTrue(User user);

    // Visible posts only (scheduled logic)
    List<Post> findByUserAndScheduledAtIsNullOrScheduledAtLessThanEqual(
            User user,
            LocalDateTime now
    );
}