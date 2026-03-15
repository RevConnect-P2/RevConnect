package com.revconnect.repository;

import com.revconnect.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPost_PostIdOrderByCreatedAtDesc(Long postId);    Optional<Comment> findByCommentIdAndUser_Email(Long commentId, String email);
    Long countByPost_PostId(Long postId);
}