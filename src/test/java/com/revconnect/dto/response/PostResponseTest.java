package com.revconnect.dto.response;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class PostResponseTest {

    @Test
    void shouldTestGettersAndSetters() {

        PostResponse response = new PostResponse();

        LocalDateTime now = LocalDateTime.now();

        response.setPostId(1L);
        response.setContent("Test post");
        response.setPostType("TEXT");
        response.setPinned(true);
        response.setCtaText("Buy Now");
        response.setCtaLink("https://example.com");
        response.setScheduledAt(now);
        response.setCreatedAt(now);

        response.setUserId(10L);
        response.setUsername("john");

        response.setHashtags(Arrays.asList("java", "spring"));
        response.setTags(Arrays.asList(new TagResponse()));

        response.setLikeCount(100L);
        response.setLikedByCurrentUser(true);
        response.setCommentCount(20L);
        response.setShareCount(5L);

        response.setSharedPost(true);
        response.setSharedByUsername("alice");
        response.setOriginalAuthorUsername("bob");

        assertEquals(1L, response.getPostId());
        assertEquals("Test post", response.getContent());
        assertEquals("TEXT", response.getPostType());
        assertTrue(response.getPinned());
        assertEquals("Buy Now", response.getCtaText());
        assertEquals("https://example.com", response.getCtaLink());
        assertEquals(now, response.getScheduledAt());
        assertEquals(now, response.getCreatedAt());

        assertEquals(10L, response.getUserId());
        assertEquals("john", response.getUsername());

        assertEquals(2, response.getHashtags().size());
        assertEquals(1, response.getTags().size());

        assertEquals(100L, response.getLikeCount());
        assertTrue(response.isLikedByCurrentUser());
        assertEquals(20L, response.getCommentCount());
        assertEquals(5L, response.getShareCount());

        assertTrue(response.isSharedPost());
        assertEquals("alice", response.getSharedByUsername());
        assertEquals("bob", response.getOriginalAuthorUsername());
    }

    @Test
    void shouldTestBuilder() {

        LocalDateTime now = LocalDateTime.now();

        PostResponse response = PostResponse.builder()
                .postId(2L)
                .content("Builder post")
                .postType("IMAGE")
                .pinned(false)
                .ctaText("Learn More")
                .ctaLink("https://learn.com")
                .scheduledAt(now)
                .createdAt(now)
                .userId(20L)
                .username("builderUser")
                .hashtags(Arrays.asList("builder", "test"))
                .tags(Arrays.asList(new TagResponse()))
                .likeCount(10L)
                .likedByCurrentUser(false)
                .commentCount(3L)
                .shareCount(1L)
                .isSharedPost(true)
                .sharedByUsername("mark")
                .originalAuthorUsername("john")
                .build();

        assertEquals(2L, response.getPostId());
        assertEquals("Builder post", response.getContent());
        assertEquals("IMAGE", response.getPostType());
        assertFalse(response.getPinned());
        assertEquals("Learn More", response.getCtaText());
        assertEquals("https://learn.com", response.getCtaLink());

        assertEquals(20L, response.getUserId());
        assertEquals("builderUser", response.getUsername());

        assertEquals(2, response.getHashtags().size());
        assertEquals(1, response.getTags().size());

        assertEquals(10L, response.getLikeCount());
        assertFalse(response.isLikedByCurrentUser());
        assertEquals(3L, response.getCommentCount());
        assertEquals(1L, response.getShareCount());

        assertTrue(response.isSharedPost());
        assertEquals("mark", response.getSharedByUsername());
        assertEquals("john", response.getOriginalAuthorUsername());
    }

    @Test
    void shouldTestDefaultValues() {

        PostResponse response = PostResponse.builder().build();

        assertEquals(0L, response.getLikeCount());
        assertEquals(0L, response.getCommentCount());
        assertEquals(0L, response.getShareCount());
        assertFalse(response.isLikedByCurrentUser());
        assertFalse(response.isSharedPost());
    }
}