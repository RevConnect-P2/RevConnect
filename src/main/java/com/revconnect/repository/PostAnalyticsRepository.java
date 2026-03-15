package com.revconnect.repository;

import com.revconnect.entity.Post;
import com.revconnect.entity.PostAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostAnalyticsRepository extends JpaRepository<PostAnalytics, Long> {

    // =============================
    // FIND ANALYTICS FOR ONE POST
    // =============================
    Optional<PostAnalytics> findByPost(Post post);

    void deleteByPost_PostId(Long postId);


    // =============================
    // GET TOTAL ANALYTICS FOR USER
    // =============================
    @Query("""
    SELECT 
        COALESCE(SUM(pa.totalLikes), 0),
        COALESCE(SUM(pa.totalComments), 0),
        COALESCE(SUM(pa.totalShares), 0)
    FROM PostAnalytics pa
    WHERE pa.post.user.userId = :userId
""")
    java.util.List<Object[]> getUserAnalytics(@Param("userId") Long userId); // Changed to List

}