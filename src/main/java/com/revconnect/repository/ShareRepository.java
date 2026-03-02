package com.revconnect.repository;

import com.revconnect.entity.Share;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShareRepository extends JpaRepository<Share, Long> {

    Optional<Share> findByOriginalPost_PostIdAndSharedBy_Email(Long postId, String email);

    Long countByOriginalPost_PostId(Long postId);
}