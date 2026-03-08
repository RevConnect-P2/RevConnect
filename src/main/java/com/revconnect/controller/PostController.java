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

    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            @RequestParam("userId") Long userId,
            @RequestBody PostCreateRequest request
    ) {
        return ResponseEntity.ok(postService.createPost(userId, request));
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable("postId") Long postId,
            @RequestParam("userId") Long userId,
            @RequestBody PostCreateRequest request
    ) {
        return ResponseEntity.ok(postService.updatePost(postId, userId, request));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePost(
            @PathVariable("postId") Long postId,
            @RequestParam("userId") Long userId
    ) {
        postService.deletePost(postId, userId);
        return ResponseEntity.ok("Post deleted successfully");
    }

    @GetMapping("/my/data")
    public ResponseEntity<List<PostResponse>> getMyPosts(
            @RequestParam("userId") Long userId
    ) {
        return ResponseEntity.ok(postService.getPostsByUser(userId));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPostById(
            @PathVariable("postId") Long postId
    ) {
        return ResponseEntity.ok(postService.getPostById(postId));
    }

    @PutMapping("/{postId}/pin")
    public ResponseEntity<PostResponse> pinPost(
            @PathVariable("postId") Long postId,
            @RequestParam("userId") Long userId
    ) {
        return ResponseEntity.ok(postService.pinPost(postId, userId));
    }

    @PutMapping("/{postId}/unpin")
    public ResponseEntity<PostResponse> unpinPost(
            @PathVariable("postId") Long postId,
            @RequestParam("userId") Long userId
    ) {
        return ResponseEntity.ok(postService.unpinPost(postId, userId));
    }

    @GetMapping("/feed")
    public ResponseEntity<List<PostResponse>> getGlobalFeed(
            @RequestParam("viewerId") Long viewerId
    ) {
        return ResponseEntity.ok(postService.getGlobalFeed(viewerId));
    }

    @PostMapping("/{postId}/save")
    public ResponseEntity<String> savePost(
            @PathVariable("postId") Long postId,
            @RequestParam("userId") Long userId
    ) {
        savedPostService.savePost(userId, postId);
        return ResponseEntity.ok("Post saved successfully");
    }

    @DeleteMapping("/{postId}/unsave")
    public ResponseEntity<String> unsavePost(
            @PathVariable("postId") Long postId,
            @RequestParam("userId") Long userId
    ) {
        savedPostService.unsavePost(userId, postId);
        return ResponseEntity.ok("Post unsaved successfully");
    }

    @GetMapping("/saved")
    public ResponseEntity<List<PostResponse>> getSavedPosts(
            @RequestParam("userId") Long userId
    ) {
        return ResponseEntity.ok(savedPostService.getSavedPosts(userId));
    }
}