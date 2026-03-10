package com.revconnect.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PostAnalyticsTest {

    @Test
    void shouldTestGettersAndSetters() {

        PostAnalytics analytics = new PostAnalytics();

        Post post = new Post();
        post.setPostId(1L);

        analytics.setId(5L);
        analytics.setPost(post);
        analytics.setTotalLikes(10L);
        analytics.setTotalComments(3L);
        analytics.setTotalShares(2L);
        analytics.setReachCount(100L);

        assertEquals(5L, analytics.getId());
        assertEquals(post, analytics.getPost());
        assertEquals(10L, analytics.getTotalLikes());
        assertEquals(3L, analytics.getTotalComments());
        assertEquals(2L, analytics.getTotalShares());
        assertEquals(100L, analytics.getReachCount());
    }

    @Test
    void shouldTestBuilder() {

        Post post = new Post();
        post.setPostId(2L);

        PostAnalytics analytics = PostAnalytics.builder()
                .id(1L)
                .post(post)
                .totalLikes(20L)
                .totalComments(5L)
                .totalShares(1L)
                .reachCount(200L)
                .build();

        assertEquals(1L, analytics.getId());
        assertEquals(post, analytics.getPost());
        assertEquals(20L, analytics.getTotalLikes());
        assertEquals(5L, analytics.getTotalComments());
        assertEquals(1L, analytics.getTotalShares());
        assertEquals(200L, analytics.getReachCount());
    }

    @Test
    void shouldTestPrePersist() {

        PostAnalytics analytics = new PostAnalytics();

        analytics.onCreate();

        assertNotNull(analytics.getCreatedAt());
    }

    @Test
    void shouldTestPreUpdate() {

        PostAnalytics analytics = new PostAnalytics();

        analytics.onUpdate();

        assertNotNull(analytics.getUpdatedAt());
    }

    @Test
    void shouldTestDefaultValues() {

        PostAnalytics analytics = PostAnalytics.builder().build();

        assertNull(analytics.getTotalLikes());
        assertNull(analytics.getTotalComments());
        assertNull(analytics.getTotalShares());
        assertNull(analytics.getReachCount());
    }
}