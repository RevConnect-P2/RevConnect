package com.revconnect.controller;

import com.revconnect.dto.request.PostCreateRequest;
import com.revconnect.dto.response.PostResponse;
import com.revconnect.service.PostService;
import com.revconnect.service.SavedPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final SavedPostService savedPostService;

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


    // =========================
    // GLOBAL FEED (JSON)
    // =========================
        @GetMapping("/feed")
        public ResponseEntity<List<PostResponse>> getGlobalFeed(
                @RequestParam Long viewerId
        ) {
            return ResponseEntity.ok(postService.getGlobalFeed(viewerId));
    }

    // =========================
    // SAVE POST
    // =========================
        @PostMapping("/{postId}/save")
        public ResponseEntity<String> savePost(
                @PathVariable Long postId,
                @RequestParam Long userId
        ) {
            savedPostService.savePost(userId, postId);
            return ResponseEntity.ok("Post saved successfully");
        }

        // =========================
    // UNSAVE POST
    // =========================
        @DeleteMapping("/{postId}/unsave")
        public ResponseEntity<String> unsavePost(
                @PathVariable Long postId,
                @RequestParam Long userId
        ) {
            savedPostService.unsavePost(userId, postId);
            return ResponseEntity.ok("Post unsaved successfully");
        }

        // =========================
    // GET SAVED POSTS
    // =========================
        @GetMapping("/saved")
        public ResponseEntity<List<PostResponse>> getSavedPosts(
                @RequestParam Long userId
        ) {
            return ResponseEntity.ok(savedPostService.getSavedPosts(userId));
        }
}