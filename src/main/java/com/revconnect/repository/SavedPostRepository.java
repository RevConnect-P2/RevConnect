package com.revconnect.repository;

import com.revconnect.entity.Post;
import com.revconnect.entity.SavedPost;
import com.revconnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SavedPostRepository extends JpaRepository<SavedPost, Long> {

    // Check if a post is already saved by a user
    Optional<SavedPost> findByUserAndPost(User user, Post post);

    // Get all saved posts of a user
    List<SavedPost> findByUser(User user);

    // Delete saved post (unsave)
    void deleteByUserAndPost(User user, Post post);

    void deleteByPost_PostId(Long postId);
}