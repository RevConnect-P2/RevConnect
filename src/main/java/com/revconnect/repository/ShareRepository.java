package com.revconnect.repository;

import com.revconnect.entity.Post;
import com.revconnect.entity.Share;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ShareRepository extends JpaRepository<Share, Long> {

    // CHECK IF USER ALREADY SHARED
    Optional<Share> findByOriginalPost_PostIdAndSharedBy_UserId(Long postId, Long userId);


    // COUNT TOTAL SHARES
    Long countByOriginalPost_PostId(Long postId);


    // FETCH ALL SHARES FOR FEED
    List<Share> findAllByOrderByCreatedAtDesc();


    // GET SHARES FOR A POST
    List<Share> findByOriginalPost_PostId(Long postId);


    // GET USERS WHO SHARED (UI LIST)
    @Query("""
        SELECT s.sharedBy.username
        FROM Share s
        WHERE s.originalPost.postId = :postId
        ORDER BY s.createdAt DESC
    """)
    List<String> findUsernamesWhoShared(Long postId);


    // DELETE SHARES WHEN POST DELETED
    void deleteByOriginalPost(Post post);
}