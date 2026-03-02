package com.revconnect.service.impl;

import com.revconnect.entity.Post;
import com.revconnect.entity.PostLike;
import com.revconnect.entity.User;
import com.revconnect.repository.PostLikeRepository;
import com.revconnect.repository.PostRepository;
import com.revconnect.repository.UserRepository;
import com.revconnect.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostLikeRepository postLikeRepository;

    @Override
    public void likePost(Long postId, String email) {

        // 1. Fetch user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Fetch post
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // 3. Check already liked
        Optional<PostLike> existingLike =
                postLikeRepository.findByPost_PostIdAndUser_Email(postId, email);

        if (existingLike.isPresent()) {
            throw new RuntimeException("You have already liked this post");
        }

        // 4. Create like
        PostLike like = PostLike.builder()
                .post(post)
                .user(user)
                .build();

        postLikeRepository.save(like);
    }

    @Override
    public void unlikePost(Long postId, String email) {

        // 1️⃣ Fetch user using email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2️⃣ Find like using postId + email
        PostLike like = postLikeRepository
                .findByPost_PostIdAndUser_Email(postId, email)
                .orElseThrow(() -> new RuntimeException("Like not found"));

        // 3️⃣ Delete like
        postLikeRepository.delete(like);
    }
}
