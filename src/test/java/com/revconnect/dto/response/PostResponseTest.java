package com.revconnect.dto.response;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PostResponseTest {

    @Test
    void testSettersAndGetters() {

        PostResponse response = new PostResponse();

        LocalDateTime now = LocalDateTime.now();

        response.setPostId(1L);
        response.setContent("Sample post");
        response.setPostType("NORMAL");
        response.setPinned(true);
        response.setCtaText("Buy Now");
        response.setCtaLink("https://example.com");
        response.setScheduledAt(now);
        response.setCreatedAt(now);
        response.setUserId(10L);
        response.setUsername("john");
        response.setHashtags(List.of("#java", "#spring"));
        response.setTags(List.of());
        response.setLikeCount(5L);
        response.setLikedByCurrentUser(true);
        response.setCommentCount(3L);
        response.setShareCount(2L);
        response.setSharedPost(true);
        response.setSharedByUsername("alice");
        response.setOriginalAuthorUsername("bob");

        assertEquals(1L, response.getPostId());
        assertEquals("Sample post", response.getContent());
        assertEquals("NORMAL", response.getPostType());
        assertTrue(response.getPinned());
        assertEquals("Buy Now", response.getCtaText());
        assertEquals("https://example.com", response.getCtaLink());
        assertEquals(now, response.getScheduledAt());
        assertEquals(now, response.getCreatedAt());
        assertEquals(10L, response.getUserId());
        assertEquals("john", response.getUsername());
        assertEquals(2, response.getHashtags().size());
        assertEquals(5L, response.getLikeCount());
        assertTrue(response.isLikedByCurrentUser());
        assertEquals(3L, response.getCommentCount());
        assertEquals(2L, response.getShareCount());
        assertTrue(response.isSharedPost());
        assertEquals("alice", response.getSharedByUsername());
        assertEquals("bob", response.getOriginalAuthorUsername());
    }

    @Test
    void testAllArgsConstructor() {

        LocalDateTime now = LocalDateTime.now();

        PostResponse response =
                new PostResponse(
                        1L,
                        "Post content",
                        "NORMAL",
                        false,
                        "Click",
                        "https://site.com",
                        now,
                        now,
                        10L,
                        "john",
                        List.of("#java"),
                        List.of(),
                        5L,
                        false,
                        2L,
                        1L,
                        false,
                        "alice",
                        "bob"
                );

        assertEquals("Post content", response.getContent());
        assertEquals("NORMAL", response.getPostType());
        assertEquals(10L, response.getUserId());
    }

    @Test
    void testBuilderAndDefaultValues() {

        PostResponse response = PostResponse.builder()
                .postId(1L)
                .content("Builder post")
                .postType("NORMAL")
                .username("builderUser")
                .build();

        assertEquals("Builder post", response.getContent());
        assertEquals("NORMAL", response.getPostType());

        // default builder values
        assertEquals(0L, response.getLikeCount());
        assertFalse(response.isLikedByCurrentUser());
        assertEquals(0L, response.getCommentCount());
        assertEquals(0L, response.getShareCount());
        assertFalse(response.isSharedPost());
    }
}