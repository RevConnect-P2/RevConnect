package com.revconnect.controller;

import com.revconnect.dto.request.PostCreateRequest;
import com.revconnect.dto.response.PostResponse;
import com.revconnect.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Posts", description = "Post management APIs")
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    /**
     * Create a new post
     */
    @Operation(summary = "Create a new post")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Post created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
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
    @Operation(summary = "Update post by postId")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Post created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
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
    @Operation(summary = "Delete a post by postId")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Post created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
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
    @Operation(summary = "Get a posts by user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Post created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
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
    @Operation(summary = "Get a post by Id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Post created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPostById(
            @PathVariable Long postId
    ) {
        PostResponse response = postService.getPostById(postId);
        return ResponseEntity.ok(response);
    }
}