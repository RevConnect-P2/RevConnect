package com.revconnect.controller;

import com.revconnect.repository.PostLikeRepository;
import com.revconnect.service.CommentService;
import com.revconnect.service.LikeService;
import com.revconnect.service.ShareService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class SocialInteractionController {

    private final PostLikeRepository postLikeRepository;
    private final LikeService likeService;
    private final CommentService commentService;
    private final ShareService shareService;

    // ================= LIKE =================
    @PostMapping("/{postId}/like")
    public ResponseEntity<?> toggleLike(@PathVariable Long postId,
                                        Principal principal) {

        if (principal == null) {
            throw new RuntimeException("User not authenticated");
        }

        System.out.println("LIKE API HIT");

        boolean liked = likeService.toggleLike(postId, principal.getName());

        long likeCount =
                postLikeRepository.countByPost_PostId(postId);

        return ResponseEntity.ok(
                Map.of(
                        "liked", liked,
                        "likeCount", likeCount
                )
        );
    }

    // ================= COMMENT =================
    @PostMapping("/{postId}/comments")
    public ResponseEntity<?> addComment(@PathVariable Long postId,
                                        @RequestBody String commentText,
                                        Principal principal) {

        if (principal == null) {
            throw new RuntimeException("User not authenticated");
        }

        System.out.println("COMMENT API HIT");

        commentService.addComment(postId, principal.getName(), commentText);

        return ResponseEntity.ok("Comment added");
    }

    // ================= GET COMMENTS =================
    @GetMapping("/{postId}/comments")
    public ResponseEntity<?> getComments(@PathVariable Long postId) {

        return ResponseEntity.ok(
                commentService.getCommentsByPostId(postId)
        );
    }

    // ================= SHARE =================
    @PostMapping("/{postId}/share")
    public ResponseEntity<?> toggleShare(@PathVariable Long postId,
                                         Principal principal) {

        if (principal == null) {
            throw new RuntimeException("User not authenticated");
        }

        System.out.println("SHARE API HIT");

        boolean shared = shareService.toggleShare(postId, principal.getName());

        long shareCount = shareService.getShareCount(postId);

        return ResponseEntity.ok(
                Map.of(
                        "shared", shared,
                        "shareCount", shareCount
                )
        );
    }

    // ================= USERS WHO LIKED =================
    @GetMapping("/{postId}/likes")
    public ResponseEntity<?> getUsersWhoLiked(@PathVariable Long postId) {

        return ResponseEntity.ok(
                likeService.getUsersWhoLiked(postId)
        );
    }
}