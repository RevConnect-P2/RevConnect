package com.revconnect.controller;

import com.revconnect.service.CommentService;
import com.revconnect.service.LikeService;
import com.revconnect.service.ShareService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class SocialInteractionController {

    private final LikeService likeService;
    private final CommentService commentService;
    private final ShareService shareService;

    @PostMapping("/{postId}/like")
    public ResponseEntity<?> likePost(@PathVariable Long postId) {

        var authentication =
                org.springframework.security.core.context.SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("User not authenticated");
        }

        String email = authentication.getName();

        likeService.likePost(postId, email);

        return ResponseEntity.ok("Post liked successfully");
    }

    @DeleteMapping("/{postId}/unlike")
    public ResponseEntity<?> unlikePost(@PathVariable Long postId,
                                        Principal principal) {

        likeService.unlikePost(postId, principal.getName());
        return ResponseEntity.ok("Post unliked successfully");
    }


//    Comment
@PostMapping("/{postId}/comments")
public ResponseEntity<?> addComment(@PathVariable Long postId,
                                    @RequestBody String commentText) {

    var authentication =
            org.springframework.security.core.context.SecurityContextHolder
                    .getContext()
                    .getAuthentication();

    String email = authentication.getName();

    commentService.addComment(postId, email, commentText);

    return ResponseEntity.ok("Comment added successfully");
}

    @GetMapping("/{postId}/comments")
    public ResponseEntity<?> getComments(@PathVariable Long postId) {

        return ResponseEntity.ok(commentService.getCommentsByPost(postId));
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId) {

        var authentication =
                org.springframework.security.core.context.SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String email = authentication.getName();

        commentService.deleteComment(commentId, email);

        return ResponseEntity.ok("Comment deleted successfully");
    }

//    Share

    @PostMapping("/{postId}/share")
    public ResponseEntity<?> sharePost(@PathVariable Long postId) {

        var authentication =
                org.springframework.security.core.context.SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String email = authentication.getName();

        shareService.sharePost(postId, email);

        return ResponseEntity.ok("Post shared successfully");
    }

    @GetMapping("/{postId}/shares/count")
    public ResponseEntity<?> getShareCount(@PathVariable Long postId) {

        return ResponseEntity.ok(shareService.getShareCount(postId));
    }

}
