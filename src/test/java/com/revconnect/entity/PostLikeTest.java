package com.revconnect.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PostLikeTest {

    @Test
    void shouldSetAndGetFieldsCorrectly() {

        Post post = new Post();
        post.setPostId(1L);

        User user = new User();
        user.setUserId(2L);

        PostLike like = new PostLike();

        like.setLikeId(10L);
        like.setPost(post);
        like.setUser(user);

        assertEquals(10L, like.getLikeId());
        assertEquals(post, like.getPost());
        assertEquals(user, like.getUser());
    }

    @Test
    void shouldBuildPostLikeUsingBuilder() {

        Post post = new Post();
        post.setPostId(1L);

        User user = new User();
        user.setUserId(2L);

        PostLike like = PostLike.builder()
                .likeId(5L)
                .post(post)
                .user(user)
                .build();

        assertEquals(5L, like.getLikeId());
        assertEquals(post, like.getPost());
        assertEquals(user, like.getUser());
    }

    @Test
    void shouldSetCreatedAtOnPrePersist() {

        Post post = new Post();
        post.setPostId(1L);

        User user = new User();
        user.setUserId(2L);

        PostLike like = new PostLike();
        like.setPost(post);
        like.setUser(user);

        like.onCreate();

        assertNotNull(like.getCreatedAt());
    }

    @Test
    void shouldSetUpdatedAtOnPreUpdate() {

        Post post = new Post();
        post.setPostId(1L);

        User user = new User();
        user.setUserId(2L);

        PostLike like = new PostLike();
        like.setPost(post);
        like.setUser(user);

        like.onUpdate();

        assertNotNull(like.getUpdatedAt());
    }
}