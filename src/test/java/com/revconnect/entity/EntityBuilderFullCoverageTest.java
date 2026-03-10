package com.revconnect.entity;

import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;

public class EntityBuilderFullCoverageTest {

    @Test
    public void testPostBuilderFullCoverage() {

        User user = new User();
        user.setUserId(1L);

        Post post = Post.builder()
                .postId(10L)
                .user(user)
                .content("Test Post")
                .postType("NORMAL")
                .pinned(true)
                .ctaText("Buy Now")
                .ctaLink("http://test.com")
                .scheduledAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .comments(List.of())
                .likes(List.of())
                .shares(List.of())
                .hashtags(List.of())
                .tags(List.of())
                .savedPosts(List.of())
                .build();

        assertEquals("Test Post", post.getContent());
        assertTrue(post.getPinned());

        post.toString(); // cover toString
    }

    @Test
    public void testUserBuilderFullCoverage() {

        User user = User.builder()
                .userId(1L)
                .username("john")
                .email("john@test.com")
                .password("123")
                .build();

        assertEquals("john", user.getUsername());

        user.toString();
    }

    @Test
    public void testConnectionBuilderCoverage() {

        User u1 = new User();
        u1.setUserId(1L);

        User u2 = new User();
        u2.setUserId(2L);

        Connection connection = Connection.builder()
                .connectionId(5L)
                .sender(u1)
                .receiver(u2)
                .build();

        assertEquals(u1, connection.getSender());

        connection.toString();
    }

    @Test
    public void testFollowBuilderCoverage() {

        User u1 = new User();
        u1.setUserId(1L);

        User u2 = new User();
        u2.setUserId(2L);

        Follow follow = Follow.builder()
                .followId(1L)
                .follower(u1)
                .following(u2)
                .build();

        assertEquals(u1, follow.getFollower());

        follow.toString();
    }

    @Test
    public void testShareBuilderCoverage() {

        Post post = new Post();
        post.setPostId(1L);

        User user = new User();
        user.setUserId(1L);

        Share share = Share.builder()
                .shareId(1L)
                .originalPost(post)
                .sharedBy(user)
                .build();

        assertEquals(user, share.getSharedBy());

        share.toString();
    }

}