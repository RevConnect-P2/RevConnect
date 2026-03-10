package com.revconnect.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserAnalyticsTest {

    @Test
    void shouldTestGettersAndSetters() {

        UserAnalytics analytics = new UserAnalytics();

        User user = new User();
        user.setUserId(1L);

        analytics.setId(10L);
        analytics.setUser(user);
        analytics.setTotalFollowers(100L);
        analytics.setTotalPosts(20L);
        analytics.setTotalEngagement(300L);

        assertEquals(10L, analytics.getId());
        assertEquals(user, analytics.getUser());
        assertEquals(100L, analytics.getTotalFollowers());
        assertEquals(20L, analytics.getTotalPosts());
        assertEquals(300L, analytics.getTotalEngagement());
    }

    @Test
    void shouldTestBuilder() {

        User user = new User();
        user.setUserId(2L);

        UserAnalytics analytics = UserAnalytics.builder()
                .id(1L)
                .user(user)
                .totalFollowers(50L)
                .totalPosts(10L)
                .totalEngagement(200L)
                .build();

        assertEquals(1L, analytics.getId());
        assertEquals(user, analytics.getUser());
        assertEquals(50L, analytics.getTotalFollowers());
        assertEquals(10L, analytics.getTotalPosts());
        assertEquals(200L, analytics.getTotalEngagement());
    }

    @Test
    void shouldTestPrePersist() {

        UserAnalytics analytics = new UserAnalytics();

        analytics.onCreate();

        assertNotNull(analytics.getCreatedAt());
    }

    @Test
    void shouldTestPreUpdate() {

        UserAnalytics analytics = new UserAnalytics();

        analytics.onUpdate();

        assertNotNull(analytics.getUpdatedAt());
    }

    @Test
    void shouldTestDefaultValues() {

        UserAnalytics analytics = UserAnalytics.builder().build();

        assertNull(analytics.getTotalFollowers());
        assertNull(analytics.getTotalPosts());
        assertNull(analytics.getTotalEngagement());
    }
}