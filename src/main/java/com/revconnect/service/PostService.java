package com.revconnect.service;

import com.revconnect.dto.request.PostCreateRequest;
import com.revconnect.dto.response.PostResponse;

import java.util.List;

public interface PostService {

    // =====================================
    // CREATE / UPDATE / DELETE POST
    // =====================================
    PostResponse createPost(Long userId, PostCreateRequest request);

    PostResponse updatePost(Long postId, Long userId, PostCreateRequest request);

    void deletePost(Long postId, Long userId);



    // =====================================
    // GET POSTS
    // =====================================
    PostResponse getPostById(Long postId);

    List<PostResponse> getPostsByUser(Long userId);

    List<PostResponse> getGlobalFeed(Long viewerUserId);



    // =====================================
    // PIN / UNPIN POST
    // =====================================
    PostResponse pinPost(Long postId, Long userId);

    PostResponse unpinPost(Long postId, Long userId);



    // =====================================
    // PROFILE STATS
    // =====================================
    long countPostsByUser(Long userId);



    // =====================================
    // HASHTAG FEATURES
    // =====================================
    List<String> getTrendingHashtags();

    List<PostResponse> getPostsByHashtag(String hashtag);



    // =====================================
    // SHARE / UNSHARE POST
    // =====================================
}