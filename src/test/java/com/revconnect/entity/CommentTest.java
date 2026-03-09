package com.revconnect.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommentTest {

    @Test
    void shouldSetAndGetFieldsCorrectly() {

        Post post = new Post();
        post.setPostId(1L);

        User user = new User();
        user.setUserId(2L);

        Comment comment = new Comment();

        comment.setCommentId(10L);
        comment.setPost(post);
        comment.setUser(user);
        comment.setCommentText("Nice post");

        assertEquals(10L, comment.getCommentId());
        assertEquals(post, comment.getPost());
        assertEquals(user, comment.getUser());
        assertEquals("Nice post", comment.getCommentText());
    }

    @Test
    void shouldBuildCommentUsingBuilder() {

        Post post = new Post();
        post.setPostId(1L);

        User user = new User();
        user.setUserId(2L);

        Comment comment = Comment.builder()
                .commentId(5L)
                .post(post)
                .user(user)
                .commentText("Great content")
                .build();

        assertEquals(5L, comment.getCommentId());
        assertEquals(post, comment.getPost());
        assertEquals(user, comment.getUser());
    }

    @Test
    void shouldSetTimestampsOnCreate() {

        Post post = new Post();
        post.setPostId(1L);

        User user = new User();
        user.setUserId(2L);

        Comment comment = new Comment();
        comment.setPost(post);
        comment.setUser(user);
        comment.setCommentText("Nice post");

        comment.onCreate();

        assertNotNull(comment.getCreatedAt());
        assertNotNull(comment.getUpdatedAt());
    }

    @Test
    void shouldSetUpdatedAtOnUpdate() {

        Post post = new Post();
        post.setPostId(1L);

        User user = new User();
        user.setUserId(2L);

        Comment comment = new Comment();
        comment.setPost(post);
        comment.setUser(user);
        comment.setCommentText("Nice post");

        comment.onUpdate();

        assertNotNull(comment.getUpdatedAt());
    }

    @Test
    void shouldThrowExceptionWhenCommentIsEmpty() {

        Post post = new Post();
        post.setPostId(1L);

        User user = new User();
        user.setUserId(2L);

        Comment comment = new Comment();
        comment.setPost(post);
        comment.setUser(user);
        comment.setCommentText("   ");

        assertThrows(
                IllegalStateException.class,
                comment::onCreate
        );
    }
}