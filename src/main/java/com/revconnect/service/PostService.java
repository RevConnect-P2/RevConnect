package com.revconnect.service;

import com.revconnect.dto.request.PostCreateRequest;
import com.revconnect.dto.response.PostResponse;

import java.util.List;

public interface PostService {

    /**
     * Create a new post
     */
    PostResponse createPost(Long userId, PostCreateRequest request);

    /**
     * Update an existing post (only owner)
     */
    PostResponse updatePost(Long postId, Long userId, PostCreateRequest request);

    /**
     * Delete a post (only owner)
     */
    void deletePost(Long postId, Long userId);

    /**
     * Get all posts created by a user
     */
    List<PostResponse> getPostsByUser(Long userId);

    /**
     * Get a post by its ID
     */
    PostResponse getPostById(Long postId);
}