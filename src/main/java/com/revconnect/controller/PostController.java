package com.revconnect.controller;

import com.revconnect.dto.request.PostCreateRequest;
import com.revconnect.dto.response.PostResponse;
import com.revconnect.service.PostService;
import com.revconnect.service.SavedPostService;
import com.revconnect.service.ShareService;

import jakarta.validation.Valid;
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

    // ✅ NEW
    private final ShareService shareService;

    // =========================
    // CREATE POST
    // =========================
    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            @RequestParam Long userId,
            @Valid @RequestBody PostCreateRequest request
    ) {
        return ResponseEntity.ok(postService.createPost(userId, request));
    }

    // =========================
    // UPDATE POST
    // =========================
    @PutMapping("/{postId}")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable Long postId,
            @RequestParam Long userId,
            @Valid @RequestBody PostCreateRequest request
    ) {
        return ResponseEntity.ok(postService.updatePost(postId, userId, request));
    }

    // =========================
    // DELETE POST
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
    // GET POST BY ID
    // =========================
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPostById(
            @PathVariable Long postId
    ) {
        return ResponseEntity.ok(postService.getPostById(postId));
    }

    // =========================
    // GET USER POSTS
    // =========================
    @GetMapping("/user")
    public ResponseEntity<List<PostResponse>> getPostsByUser(
            @RequestParam Long userId
    ) {
        return ResponseEntity.ok(postService.getPostsByUser(userId));
    }

    // =========================
    // GLOBAL FEED
    // =========================
    @GetMapping("/feed")
    public ResponseEntity<List<PostResponse>> getGlobalFeed(
            @RequestParam Long viewerUserId
    ) {
        return ResponseEntity.ok(postService.getGlobalFeed(viewerUserId));
    }

    // =========================
    // PIN POST
    // =========================
    @PutMapping("/{postId}/pin")
    public ResponseEntity<PostResponse> pinPost(
            @PathVariable Long postId,
            @RequestParam Long userId
    ) {
        return ResponseEntity.ok(postService.pinPost(postId, userId));
    }

    // =========================
    // UNPIN POST
    // =========================
    @PutMapping("/{postId}/unpin")
    public ResponseEntity<PostResponse> unpinPost(
            @PathVariable Long postId,
            @RequestParam Long userId
    ) {
        return ResponseEntity.ok(postService.unpinPost(postId, userId));
    }



    // =========================
    // GET POSTS BY HASHTAG
    // =========================
    @GetMapping("/hashtag/{tag}")
    public ResponseEntity<List<PostResponse>> getPostsByHashtag(
            @PathVariable String tag
    ) {
        return ResponseEntity.ok(postService.getPostsByHashtag(tag));
    }

    // =========================
    // TRENDING HASHTAGS
    // =========================
    @GetMapping("/hashtags/trending")
    public ResponseEntity<List<String>> getTrendingHashtags() {
        return ResponseEntity.ok(postService.getTrendingHashtags());
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