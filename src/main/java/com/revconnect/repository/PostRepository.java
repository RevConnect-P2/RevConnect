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

    // ================================
    // 1️⃣ Find pinned post
    // ================================
    Optional<Post> findByUserAndPinnedTrue(User user);


    // ================================
    // 2️⃣ Visible posts of logged-in user (Profile Page)
    // ================================
    @Query("""
        SELECT p FROM Post p
        WHERE p.user = :user
        AND (p.scheduledAt IS NULL OR p.scheduledAt <= :now)
        ORDER BY p.createdAt DESC
    """)
    List<Post> findVisiblePostsByUser(
            @Param("user") User user,
            @Param("now") LocalDateTime now
    );


    // ================================
    // 3️⃣ ✅ GLOBAL FEED (FIXED VERSION)
    // ================================
    @Query("""
        SELECT p FROM Post p
        JOIN p.user u
        JOIN u.userProfile up
        WHERE
            (p.scheduledAt IS NULL OR p.scheduledAt <= :now)
        AND
            (
                up.profileVisibility = 'PUBLIC'
                OR u.userId = :currentUserId
            )
        ORDER BY p.createdAt DESC
    """)
    List<Post> findGlobalFeedPosts(
            @Param("currentUserId") Long currentUserId,
            @Param("now") LocalDateTime now
    );


    // ================================
    // 4️⃣ Profile post count
    // ================================
    long countByUser(User user);

    List<Post> findByScheduledAtIsNullOrScheduledAtLessThanEqual(LocalDateTime now);
}