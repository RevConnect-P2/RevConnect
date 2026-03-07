package com.revconnect.service;

import com.revconnect.dto.request.PostCreateRequest;
import com.revconnect.dto.response.PostResponse;

import java.util.List;

public interface PostService {

    // =====================================
    // Create post
    // =====================================
    PostResponse createPost(Long userId, PostCreateRequest request);


    // =====================================
    // Update post
    // =====================================
    PostResponse updatePost(Long postId, Long userId, PostCreateRequest request);


    // =====================================
    // Delete post
    // =====================================
    void deletePost(Long postId, Long userId);


    // =====================================
    // Get posts by user (profile page)
    // =====================================
    List<PostResponse> getPostsByUser(Long userId);


    // =====================================
    // Get post by id
    // =====================================
    PostResponse getPostById(Long postId);


    // =====================================
    // Pin / Unpin post
    // =====================================
    PostResponse pinPost(Long postId, Long userId);

    PostResponse unpinPost(Long postId, Long userId);


    // =====================================
    // Global feed
    // =====================================
    List<PostResponse> getGlobalFeed(Long viewerUserId);


    // =====================================
    // Count posts (profile stats)
    // =====================================
    long countPostsByUser(Long userId);


    // ====================================================
    // NEW FEATURE 1️⃣ Trending hashtags (for sidebar)
    // ====================================================
    List<String> getTrendingHashtags();


    // ====================================================
    // NEW FEATURE 2️⃣ Get posts by hashtag
    // ====================================================
    List<PostResponse> getPostsByHashtag(String hashtag);
    // =====================================
// SHARE / UNSHARE POST
// =====================================
    Long toggleShare(Long postId, Long userId);

}