package com.revconnect.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PostTest {

    @Test
    void shouldSetAndGetFieldsCorrectly() {

        User user = new User();
        user.setUserId(1L);

        Post post = new Post();

        post.setPostId(10L);
        post.setUser(user);
        post.setContent("Hello world");
        post.setPostType("TEXT");
        post.setPinned(true);
        post.setCtaText("Click here");
        post.setCtaLink("https://example.com");

        assertEquals(10L, post.getPostId());
        assertEquals(user, post.getUser());
        assertEquals("Hello world", post.getContent());
        assertEquals("TEXT", post.getPostType());
        assertTrue(post.getPinned());
        assertEquals("Click here", post.getCtaText());
        assertEquals("https://example.com", post.getCtaLink());
    }

    @Test
    void shouldBuildPostUsingBuilder() {

        User user = new User();
        user.setUserId(1L);

        Post post = Post.builder()
                .postId(1L)
                .user(user)
                .content("Builder content")
                .postType("IMAGE")
                .pinned(false)
                .build();

        assertEquals("Builder content", post.getContent());
        assertEquals("IMAGE", post.getPostType());
        assertFalse(post.getPinned());
    }

    @Test
    void shouldInitializeRelationshipLists() {

        Post post = new Post();

        assertNotNull(post.getComments());
        assertNotNull(post.getLikes());
        assertNotNull(post.getShares());
        assertNotNull(post.getHashtags());
        assertNotNull(post.getTags());
        assertNotNull(post.getSavedPosts());
    }

    @Test
    void shouldSetCreatedAtOnPrePersist() {

        Post post = new Post();

        post.onCreate();

        assertNotNull(post.getCreatedAt());
    }

    @Test
    void shouldSetUpdatedAtOnPreUpdate() {

        Post post = new Post();

        post.onUpdate();

        assertNotNull(post.getUpdatedAt());
    }

    @Test
    void shouldSetPinnedFalseIfNullOnCreate() {

        Post post = new Post();
        post.setPinned(null);

        post.onCreate();

        assertFalse(post.getPinned());
    }
}