package com.revconnect.repository;

import com.revconnect.entity.Share;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ShareRepository extends JpaRepository<Share, Long> {

    // check if user already shared the post
    Optional<Share> findByOriginalPost_PostIdAndSharedBy_UserId(Long postId, Long userId);

    // count total shares
    Long countByOriginalPost_PostId(Long postId);

    // used for global feed shared posts
    List<Share> findAllByOrderByCreatedAtDesc();

    // used for share list UI
    List<Share> findByOriginalPost_PostId(Long postId);
}