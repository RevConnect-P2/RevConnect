package com.revconnect.service.impl;

import com.revconnect.dto.response.PostResponse;
import com.revconnect.entity.Post;
import com.revconnect.entity.SavedPost;
import com.revconnect.entity.User;
import com.revconnect.exception.BadRequestException;
import com.revconnect.exception.ResourceNotFoundException;
import com.revconnect.mapper.PostMapper;
import com.revconnect.repository.PostRepository;
import com.revconnect.repository.SavedPostRepository;
import com.revconnect.repository.UserRepository;
import com.revconnect.service.SavedPostService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

// LOGGER IMPORTS
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
@RequiredArgsConstructor
public class SavedPostServiceImpl implements SavedPostService {

    private static final Logger logger =
            LogManager.getLogger(SavedPostServiceImpl.class);

    private final SavedPostRepository savedPostRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostMapper postMapper;


    // SAVE POST

    @Override
    @Transactional
    public void savePost(Long userId, Long postId) {

        logger.info("User {} attempting to save post {}", userId, postId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User not found with id {}", userId);
                    return new ResourceNotFoundException("User not found");
                });

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> {
                    logger.error("Post not found with id {}", postId);
                    return new ResourceNotFoundException("Post not found");
                });

        savedPostRepository.findByUserAndPost(user, post)
                .ifPresent(sp -> {

                    logger.warn("User {} already saved post {}", userId, postId);

                    throw new BadRequestException("Post already saved");
                });

        SavedPost savedPost = SavedPost.builder()
                .user(user)
                .post(post)
                .savedAt(LocalDateTime.now())
                .build();

        savedPostRepository.save(savedPost);

        logger.info("Post {} saved successfully by user {}", postId, userId);
    }


    // UNSAVE POST

    @Override
    @Transactional
    public void unsavePost(Long userId, Long postId) {

        logger.info("User {} attempting to unsave post {}", userId, postId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User not found with id {}", userId);
                    return new ResourceNotFoundException("User not found");
                });

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> {
                    logger.error("Post not found with id {}", postId);
                    return new ResourceNotFoundException("Post not found");
                });

        savedPostRepository.deleteByUserAndPost(user, post);

        logger.info("Post {} unsaved successfully by user {}", postId, userId);
    }

    // GET SAVED POSTS

    @Override
    public List<PostResponse> getSavedPosts(Long userId) {

        logger.info("Fetching saved posts for user {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User not found with id {}", userId);
                    return new ResourceNotFoundException("User not found");
                });

        List<PostResponse> savedPosts = savedPostRepository.findByUser(user)
                .stream()
                .map(savedPost ->
                        postMapper.toPostResponse(
                                savedPost.getPost(),
                                List.of(),
                                List.of()
                        )
                )
                .toList();

        logger.info("User {} has {} saved posts", userId, savedPosts.size());

        return savedPosts;
    }
}