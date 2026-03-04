package com.revconnect.repository;

import com.revconnect.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.revconnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {

    // ✅ Personalized Feed
    @Query("""
    SELECT p FROM Post p
    WHERE p.user.userId IN (
        SELECT f.following.userId FROM Follow f WHERE f.follower.userId = :userId
    )
    OR p.user.userId IN (
        SELECT c.receiver.userId FROM Connection c
        WHERE c.sender.userId = :userId AND c.status = 'ACCEPTED'
    )
    OR p.user.userId = :userId
    """)
    Page<Post> getPersonalizedFeed(@Param("userId") Long userId,
                                   Pageable pageable);


    // ✅ Trending Posts
    @Query("""
    SELECT p FROM Post p
    ORDER BY (
        SELECT COUNT(l) FROM PostLike l
        WHERE l.post.postId = p.postId
    ) DESC
    """)
    Page<Post> findTrendingPosts(Pageable pageable);


    // ✅ Search by Hashtag
    @Query("""
    SELECT p FROM Post p
    JOIN PostHashtag ph ON ph.post = p
    JOIN Hashtag h ON ph.hashtag = h
    WHERE LOWER(h.tagName) = LOWER(:tag)
    """)
    Page<Post> findByHashtag(@Param("tag") String tag,
                             Pageable pageable);
}
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
