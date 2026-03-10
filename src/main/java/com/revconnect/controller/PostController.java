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

// ✅ LOGGER IMPORTS
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    // ✅ LOGGER OBJECT
    private static final Logger logger =
            LogManager.getLogger(PostController.class);

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

        logger.info("Create post request received from user {}", userId);

        PostResponse response = postService.createPost(userId, request);

        logger.info("Post created successfully by user {}", userId);

        return ResponseEntity.ok(response);
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

        logger.info("Update post request for post {} by user {}", postId, userId);

        PostResponse response = postService.updatePost(postId, userId, request);

        logger.info("Post {} updated successfully", postId);

        return ResponseEntity.ok(response);
    }

    // =========================
    // DELETE POST
    // =========================
    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePost(
            @PathVariable Long postId,
            @RequestParam Long userId
    ) {

        logger.info("Delete post request for post {} by user {}", postId, userId);

        postService.deletePost(postId, userId);

        logger.info("Post {} deleted successfully", postId);

        return ResponseEntity.ok("Post deleted successfully");
    }

    // =========================
    // GET POST BY ID
    // =========================
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPostById(
            @PathVariable Long postId
    ) {

        logger.info("Fetching post with ID {}", postId);

        return ResponseEntity.ok(postService.getPostById(postId));
    }

    // =========================
    // GET USER POSTS
    // =========================
    @GetMapping("/user")
    public ResponseEntity<List<PostResponse>> getPostsByUser(
            @RequestParam Long userId
    ) {

        logger.info("Fetching posts for user {}", userId);

        return ResponseEntity.ok(postService.getPostsByUser(userId));
    }

    // =========================
    // GLOBAL FEED
    // =========================
    @GetMapping("/feed")
    public ResponseEntity<List<PostResponse>> getGlobalFeed(
            @RequestParam Long viewerUserId
    ) {

        logger.info("Fetching global feed for user {}", viewerUserId);

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

        logger.info("Pin post request for post {} by user {}", postId, userId);

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

        logger.info("Unpin post request for post {} by user {}", postId, userId);

        return ResponseEntity.ok(postService.unpinPost(postId, userId));
    }

    // =========================
    // GET POSTS BY HASHTAG
    // =========================
    @GetMapping("/hashtag/{tag}")
    public ResponseEntity<List<PostResponse>> getPostsByHashtag(
            @PathVariable String tag
    ) {

        logger.info("Fetching posts for hashtag {}", tag);

        return ResponseEntity.ok(postService.getPostsByHashtag(tag));
    }

    // =========================
    // TRENDING HASHTAGS
    // =========================
    @GetMapping("/hashtags/trending")
    public ResponseEntity<List<String>> getTrendingHashtags() {

        logger.info("Fetching trending hashtags");

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

        logger.info("User {} saving post {}", userId, postId);

        savedPostService.savePost(userId, postId);

        logger.info("Post {} saved by user {}", postId, userId);

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

        logger.info("User {} unsaving post {}", userId, postId);

        savedPostService.unsavePost(userId, postId);

        logger.info("Post {} unsaved by user {}", postId, userId);

        return ResponseEntity.ok("Post unsaved successfully");
    }

    // =========================
    // GET SAVED POSTS
    // =========================
    @GetMapping("/saved")
    public ResponseEntity<List<PostResponse>> getSavedPosts(
            @RequestParam Long userId
    ) {

        logger.info("Fetching saved posts for user {}", userId);

        return ResponseEntity.ok(savedPostService.getSavedPosts(userId));
    }

    @GetMapping("/{postId}/shares")
    public ResponseEntity<List<String>> getUsersWhoShared(
            @PathVariable Long postId
    ) {

        logger.info("Fetching users who shared post {}", postId);

        return ResponseEntity.ok(shareService.getUsersWhoShared(postId));
    }

}