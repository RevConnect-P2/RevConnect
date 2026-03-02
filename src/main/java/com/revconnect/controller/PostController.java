package com.revconnect.controller;

import com.revconnect.dto.request.PostCreateRequest;
import com.revconnect.dto.response.PostResponse;
import com.revconnect.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // =========================
    // CREATE POST (JSON)
    // =========================
    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            @RequestParam Long userId,
            @RequestBody PostCreateRequest request
    ) {
        return ResponseEntity.ok(postService.createPost(userId, request));
    }

    // =========================
    // UPDATE POST (JSON)
    // =========================
    @PutMapping("/{postId}")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable Long postId,
            @RequestParam Long userId,
            @RequestBody PostCreateRequest request
    ) {
        return ResponseEntity.ok(postService.updatePost(postId, userId, request));
    }

    // =========================
    // DELETE POST (JSON)
    // =========================
    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePost(
            @PathVariable Long postId,
            @RequestParam Long userId
    ) {
        postService.deletePost(postId, userId);
        return ResponseEntity.ok("Post deleted successfully");
    }

    // =========================
    // GET MY POSTS (JSON) ✅ FIXED URL
    // =========================
    @GetMapping("/my/data")
    public ResponseEntity<List<PostResponse>> getMyPosts(
            @RequestParam Long userId
    ) {
        return ResponseEntity.ok(postService.getPostsByUser(userId));
    }

    // =========================
    // GET POST BY ID (JSON)
    // =========================
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPostById(
            @PathVariable Long postId
    ) {
        return ResponseEntity.ok(postService.getPostById(postId));
    }

    // =========================
    // PIN / UNPIN (JSON)
    // =========================
    @PutMapping("/{postId}/pin")
    public ResponseEntity<PostResponse> pinPost(
            @PathVariable Long postId,
            @RequestParam Long userId
    ) {
        return ResponseEntity.ok(postService.pinPost(postId, userId));
    }

    @PutMapping("/{postId}/unpin")
    public ResponseEntity<PostResponse> unpinPost(
            @PathVariable Long postId,
            @RequestParam Long userId
    ) {
        return ResponseEntity.ok(postService.unpinPost(postId, userId));
    }
}