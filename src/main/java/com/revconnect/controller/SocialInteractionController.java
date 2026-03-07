package com.revconnect.controller;

import com.revconnect.repository.PostLikeRepository;
import com.revconnect.service.CommentService;
import com.revconnect.service.LikeService;
import com.revconnect.service.ShareService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

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
    public long toggleLike(@PathVariable Long postId,
                           Principal principal) {

        likeService.toggleLike(postId, principal.getName());

        return postLikeRepository.countByPost_PostId(postId);
    }

//    @DeleteMapping("/{postId}/unlike")
//    public ResponseEntity<?> unlikePost(@PathVariable Long postId,
//                                        Principal principal) {
//
//        likeService.unlikePost(postId, principal.getName());
//        return ResponseEntity.ok("Post unliked");
//    }

    // ================= COMMENT =================
    @PostMapping("/{postId}/comments")
    public ResponseEntity<?> addComment(@PathVariable Long postId,
                                        @RequestBody String commentText,
                                        Principal principal) {

        commentService.addComment(postId, principal.getName(), commentText);
        return ResponseEntity.ok("Comment added");
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<?> getComments(@PathVariable Long postId) {

        return ResponseEntity.ok(
                commentService.getCommentsByPostId(postId)
        );
    }

    // ================= SHARE =================
    @PostMapping("/{postId}/share")
    public ResponseEntity<Long> sharePost(@PathVariable Long postId,
                                          Principal principal) {

        shareService.sharePost(postId, principal.getName());

        Long updatedCount = shareService.getShareCount(postId);

        return ResponseEntity.ok(updatedCount);
    }

    // ================= GET USERS WHO LIKED =================
    @GetMapping("/{postId}/likes")
    public ResponseEntity<?> getUsersWhoLiked(@PathVariable Long postId) {

        return ResponseEntity.ok(
                likeService.getUsersWhoLiked(postId)
        );
    }

    // ================= GET USERS WHO SHARED =================
    @GetMapping("/{postId}/shares")
    public ResponseEntity<?> getUsersWhoShared(@PathVariable Long postId) {

        return ResponseEntity.ok(
                shareService.getUsersWhoShared(postId)
        );
    }
}