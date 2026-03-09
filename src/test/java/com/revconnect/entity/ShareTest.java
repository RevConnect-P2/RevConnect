package com.revconnect.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShareTest {

    @Test
    void shouldSetAndGetFieldsCorrectly() {

        Post post = new Post();
        post.setPostId(1L);

        User user = new User();
        user.setUserId(2L);

        Share share = new Share();

        share.setShareId(10L);
        share.setOriginalPost(post);
        share.setSharedBy(user);

        assertEquals(10L, share.getShareId());
        assertEquals(post, share.getOriginalPost());
        assertEquals(user, share.getSharedBy());
    }

    @Test
    void shouldBuildShareUsingBuilder() {

        Post post = new Post();
        post.setPostId(1L);

        User user = new User();
        user.setUserId(2L);

        Share share = Share.builder()
                .shareId(5L)
                .originalPost(post)
                .sharedBy(user)
                .build();

        assertEquals(5L, share.getShareId());
        assertEquals(post, share.getOriginalPost());
        assertEquals(user, share.getSharedBy());
    }

    @Test
    void shouldSetCreatedAtOnPrePersist() {

        Post post = new Post();
        post.setPostId(1L);

        User user = new User();
        user.setUserId(2L);

        Share share = new Share();
        share.setOriginalPost(post);
        share.setSharedBy(user);

        share.onCreate();

        assertNotNull(share.getCreatedAt());
    }

    @Test
    void shouldSetUpdatedAtOnPreUpdate() {

        Post post = new Post();
        post.setPostId(1L);

        User user = new User();
        user.setUserId(2L);

        Share share = new Share();
        share.setOriginalPost(post);
        share.setSharedBy(user);

        share.onUpdate();

        assertNotNull(share.getUpdatedAt());
    }
}