package com.revconnect.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FollowTest {

    @Test
    void shouldSetAndGetFieldsCorrectly() {

        User follower = new User();
        follower.setUserId(1L);

        User following = new User();
        following.setUserId(2L);

        Follow follow = new Follow();

        follow.setFollowId(10L);
        follow.setFollower(follower);
        follow.setFollowing(following);

        assertEquals(10L, follow.getFollowId());
        assertEquals(follower, follow.getFollower());
        assertEquals(following, follow.getFollowing());
    }

    @Test
    void shouldBuildFollowUsingBuilder() {

        User follower = new User();
        follower.setUserId(1L);

        User following = new User();
        following.setUserId(2L);

        Follow follow = Follow.builder()
                .followId(5L)
                .follower(follower)
                .following(following)
                .build();

        assertEquals(5L, follow.getFollowId());
        assertEquals(follower, follow.getFollower());
        assertEquals(following, follow.getFollowing());
    }

    @Test
    void shouldSetCreatedAtOnPrePersist() {

        User follower = new User();
        follower.setUserId(1L);

        User following = new User();
        following.setUserId(2L);

        Follow follow = new Follow();
        follow.setFollower(follower);
        follow.setFollowing(following);

        follow.onCreate();

        assertNotNull(follow.getCreatedAt());
    }

    @Test
    void shouldSetUpdatedAtOnPreUpdate() {

        User follower = new User();
        follower.setUserId(1L);

        User following = new User();
        following.setUserId(2L);

        Follow follow = new Follow();
        follow.setFollower(follower);
        follow.setFollowing(following);

        follow.onUpdate();

        assertNotNull(follow.getUpdatedAt());
    }

    @Test
    void shouldThrowExceptionWhenUserFollowsSelf() {

        User user = new User();
        user.setUserId(1L);

        Follow follow = new Follow();
        follow.setFollower(user);
        follow.setFollowing(user);

        assertThrows(
                IllegalStateException.class,
                follow::onCreate
        );
    }
}