package com.revconnect.repository;

import com.revconnect.entity.Post;
import com.revconnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    // Find pinned post of a user
    Optional<Post> findByUserAndPinnedTrue(User user);

    // Visible posts only (scheduled logic)
    // Existing (My Posts)
//    List<Post> findByUserAndScheduledAtIsNullOrScheduledAtLessThanEqual(
//            User user,
//            LocalDateTime now
//    );

    @Query("""
    SELECT p FROM Post p
    WHERE p.user = :user
      AND (p.scheduledAt IS NULL OR p.scheduledAt <= :now)
""")
    List<Post> findVisiblePostsByUser(
            @Param("user") User user,
            @Param("now") LocalDateTime now
    );

    // New (Global Feed)
    List<Post> findByScheduledAtIsNullOrScheduledAtLessThanEqual(
            LocalDateTime now
    );
}