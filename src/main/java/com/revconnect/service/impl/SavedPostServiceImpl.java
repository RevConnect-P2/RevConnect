package com.revconnect.service.impl;

import com.revconnect.dto.response.PostResponse;
import com.revconnect.entity.Post;
import com.revconnect.entity.SavedPost;
import com.revconnect.entity.User;
import com.revconnect.exception.BadRequestException;
import com.revconnect.exception.ResourceNotFoundException;
import com.revconnect.mapper.PostMapper;
import com.revconnect.repository.*;
import com.revconnect.service.SavedPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SavedPostServiceImpl implements SavedPostService {

    private final SavedPostRepository savedPostRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;
    private final ShareRepository shareRepository;

    @Override
    @Transactional
    public void savePost(Long userId, Long postId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        savedPostRepository.findByUserAndPost(user, post)
                .ifPresent(sp -> {
                    throw new BadRequestException("Post already saved");
                });

        SavedPost savedPost = SavedPost.builder()
                .user(user)
                .post(post)
                .savedAt(LocalDateTime.now())
                .build();

        savedPostRepository.save(savedPost);
    }

    @Override
    @Transactional
    public void unsavePost(Long userId, Long postId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        savedPostRepository.deleteByUserAndPost(user, post);
    }

    @Override
    public List<PostResponse> getSavedPosts(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return savedPostRepository.findByUser(user)
                .stream()
                .map(savedPost -> {

                    Post post = savedPost.getPost();

                    PostResponse response = postMapper.toPostResponse(
                            post,
                            List.of(),
                            List.of()
                    );

                    response.setLikeCount(
                            postLikeRepository.countByPost_PostId(post.getPostId())
                    );

                    response.setCommentCount(
                            commentRepository.countByPost_PostId(post.getPostId())
                    );

                    response.setShareCount(
                            shareRepository.countByOriginalPost_PostId(post.getPostId())
                    );

                    return response;

                })
                .toList();
    }
}