package com.revconnect.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SavedPostTest {

    @Test
    void shouldSetAndGetFieldsCorrectly() {

        User user = new User();
        user.setUserId(1L);

        Post post = new Post();
        post.setPostId(2L);

        LocalDateTime now = LocalDateTime.now();

        SavedPost savedPost = new SavedPost();

        savedPost.setId(10L);
        savedPost.setUser(user);
        savedPost.setPost(post);
        savedPost.setSavedAt(now);

        assertEquals(10L, savedPost.getId());
        assertEquals(user, savedPost.getUser());
        assertEquals(post, savedPost.getPost());
        assertEquals(now, savedPost.getSavedAt());
    }

    @Test
    void shouldBuildSavedPostUsingBuilder() {

        User user = new User();
        user.setUserId(1L);

        Post post = new Post();
        post.setPostId(2L);

        LocalDateTime now = LocalDateTime.now();

        SavedPost savedPost = SavedPost.builder()
                .id(5L)
                .user(user)
                .post(post)
                .savedAt(now)
                .build();

        assertEquals(5L, savedPost.getId());
        assertEquals(user, savedPost.getUser());
        assertEquals(post, savedPost.getPost());
        assertEquals(now, savedPost.getSavedAt());
    }

}