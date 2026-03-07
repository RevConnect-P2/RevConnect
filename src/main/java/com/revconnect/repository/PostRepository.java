package com.revconnect.repository;

import com.revconnect.entity.Post;
import com.revconnect.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 1️⃣ Find pinned post
    Optional<Post> findByUserAndPinnedTrue(User user);


    // 2️⃣ Visible posts of a specific user
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


    // 3️⃣ GLOBAL FEED (SAFE VERSION)
    @Query("""
        SELECT p FROM Post p
        LEFT JOIN p.user u
        LEFT JOIN u.userProfile up
        WHERE
            (p.scheduledAt IS NULL OR p.scheduledAt <= :now)
        AND
            (
                up.profileVisibility = 'PUBLIC'
                OR u.userId = :currentUserId
                OR up IS NULL
            )
        ORDER BY p.pinned DESC, p.createdAt DESC
    """)
    List<Post> findGlobalFeedPosts(
            @Param("currentUserId") Long currentUserId,
            @Param("now") LocalDateTime now
    );


    // 4️⃣ Profile post count
    long countByUser(User user);


    // 5️⃣ Scheduled posts
    @Query("""
        SELECT p FROM Post p
        WHERE p.scheduledAt IS NOT NULL
        AND p.scheduledAt <= :now
    """)
    List<Post> findReadyScheduledPosts(@Param("now") LocalDateTime now);


    // 6️⃣ Trending hashtags
    @Query("""
        SELECT ph.hashtag.tagName
        FROM PostHashtag ph
        GROUP BY ph.hashtag.tagName
        ORDER BY COUNT(ph.post) DESC
    """)
    List<String> findTrendingHashtags(Pageable pageable);


    // 7️⃣ Find posts by hashtag
    @Query("""
        SELECT ph.post
        FROM PostHashtag ph
        WHERE ph.hashtag.tagName = :tag
        ORDER BY ph.post.createdAt DESC
    """)
    List<Post> findPostsByHashtag(@Param("tag") String tag);

}