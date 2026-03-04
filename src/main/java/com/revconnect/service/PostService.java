package com.revconnect.service;

import com.revconnect.entity.Post;
import com.revconnect.entity.User;

public interface PostService {
    void createPost(Post post, User user);
}
import com.revconnect.dto.request.PostCreateRequest;
import com.revconnect.dto.response.PostResponse;

import java.util.List;

public interface PostService {

    PostResponse createPost(Long userId, PostCreateRequest request);

    PostResponse updatePost(Long postId, Long userId, PostCreateRequest request);

    void deletePost(Long postId, Long userId);

    List<PostResponse> getPostsByUser(Long userId);

    PostResponse getPostById(Long postId);

    PostResponse pinPost(Long postId, Long userId);

    PostResponse unpinPost(Long postId, Long userId);

    List<PostResponse> getGlobalFeed(Long viewerUserId);


    // ✅ ADD THIS
    long countPostsByUser(Long userId);

}
