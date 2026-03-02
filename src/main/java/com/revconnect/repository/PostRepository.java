package com.revconnect.repository;

import com.revconnect.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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