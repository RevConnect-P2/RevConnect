package com.revconnect.repository;

import com.revconnect.entity.Post;
import com.revconnect.entity.PostLike;
import com.revconnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    Optional<PostLike> findByPost_PostIdAndUser_Username(Long postId, String email);

    Long countByPost_PostId(Long postId);

    Optional<PostLike> findByPost_PostIdAndUser_Email(Long postId, String email);

    Optional<PostLike> findByPost_PostIdAndUser_UserId(Long postId, Long userId);

    Optional<PostLike> findByUserAndPost(User user, Post post);

    List<PostLike> findByPost_PostId(Long postId);

    void deleteByPost_PostId(Long postId);

}
