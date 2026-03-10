package com.revconnect.entity;

import org.junit.Test;
import static org.junit.Assert.*;

import java.time.LocalDateTime;

public class EntityCoverageTest {

    @Test
    public void testUserEntity() {

        User user = new User();
        user.setUserId(1L);
        user.setUsername("john");
        user.setEmail("john@mail.com");
        user.setPassword("123");

        assertEquals(Long.valueOf(1), user.getUserId());
        assertEquals("john", user.getUsername());
        assertEquals("john@mail.com", user.getEmail());
    }

    @Test
    public void testPostEntity() {

        User user = new User();
        user.setUserId(1L);

        Post post = new Post();
        post.setPostId(10L);
        post.setContent("Hello");
        post.setPostType("NORMAL");
        post.setPinned(true);
        post.setUser(user);
        post.setCreatedAt(LocalDateTime.now());

        assertEquals("Hello", post.getContent());
        assertTrue(post.getPinned());
    }

    @Test
    public void testConnectionEntity() {

        User sender = new User();
        sender.setUserId(1L);

        User receiver = new User();
        receiver.setUserId(2L);

        Connection connection = new Connection();
        connection.setSender(sender);
        connection.setReceiver(receiver);

        assertEquals(sender, connection.getSender());
    }

    @Test
    public void testFollowEntity() {

        User follower = new User();
        follower.setUserId(1L);

        User following = new User();
        following.setUserId(2L);

        Follow follow = new Follow();
        follow.setFollower(follower);
        follow.setFollowing(following);

        assertEquals(follower, follow.getFollower());
    }

    @Test
    public void testShareEntity() {

        Post post = new Post();
        post.setPostId(10L);

        User user = new User();
        user.setUserId(1L);

        Share share = new Share();
        share.setOriginalPost(post);
        share.setSharedBy(user);

        assertEquals(post, share.getOriginalPost());
    }

    @Test
    public void testPostLikeEntity() {

        Post post = new Post();
        post.setPostId(10L);

        User user = new User();
        user.setUserId(1L);

        PostLike like = new PostLike();
        like.setPost(post);
        like.setUser(user);

        assertEquals(post, like.getPost());
    }

    @Test
    public void testAnalyticsEntities() {

        User user = new User();
        user.setUserId(1L);

        UserAnalytics userAnalytics = new UserAnalytics();
        userAnalytics.setUser(user);
        userAnalytics.setTotalFollowers(10L);

        assertEquals(Long.valueOf(10), userAnalytics.getTotalFollowers());

        Post post = new Post();
        post.setPostId(1L);

        PostAnalytics postAnalytics = new PostAnalytics();
        postAnalytics.setPost(post);
        postAnalytics.setTotalLikes(5L);

        assertEquals(Long.valueOf(5), postAnalytics.getTotalLikes());
    }

}