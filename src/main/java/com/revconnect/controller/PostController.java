package com.revconnect.controller;

import com.revconnect.dto.request.PostCreateRequest;
import com.revconnect.dto.response.PostResponse;
import com.revconnect.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    /**
     * Create a new post
     */
    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            @RequestParam Long userId,
            @RequestBody PostCreateRequest request
    ) {
        PostResponse response = postService.createPost(userId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Update existing post (only owner)
     */
    @PutMapping("/{postId}")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable Long postId,
            @RequestParam Long userId,
            @RequestBody PostCreateRequest request
    ) {
        PostResponse response = postService.updatePost(postId, userId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete post (only owner)
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePost(
            @PathVariable Long postId,
            @RequestParam Long userId
    ) {
        postService.deletePost(postId, userId);
        return ResponseEntity.ok("Post deleted successfully");
    }

    /**
     * Get posts created by logged-in user
     */
    @GetMapping("/my")
    public ResponseEntity<List<PostResponse>> getMyPosts(
            @RequestParam Long userId
    ) {
        List<PostResponse> posts = postService.getPostsByUser(userId);
        return ResponseEntity.ok(posts);
    }

    /**
     * Get post by ID
     */
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPostById(
            @PathVariable Long postId
    ) {
        PostResponse response = postService.getPostById(postId);
        return ResponseEntity.ok(response);
    }
}