package com.revconnect.entity;

import org.junit.Test;
import static org.junit.Assert.*;

import java.time.LocalDateTime;

public class EntityBuilderCoverageTest {

    @Test
    public void coverUserBuilder() {

        User user = User.builder()
                .userId(1L)
                .username("john")
                .email("john@mail.com")
                .password("123")
                .build();

        assertEquals("john", user.getUsername());
    }

    @Test
    public void coverPostBuilder() {

        User user = new User();
        user.setUserId(1L);

        Post post = Post.builder()
                .postId(10L)
                .content("Test content")
                .postType("NORMAL")
                .pinned(false)
                .ctaText("Buy")
                .ctaLink("link")
                .scheduledAt(LocalDateTime.now())
                .user(user)
                .build();

        assertEquals("Test content", post.getContent());
    }

    @Test
    public void coverConnectionBuilder() {

        User sender = new User();
        sender.setUserId(1L);

        User receiver = new User();
        receiver.setUserId(2L);

        Connection connection = Connection.builder()
                .connectionId(100L)
                .sender(sender)
                .receiver(receiver)
                .build();

        assertEquals(sender, connection.getSender());
    }

    @Test
    public void coverFollowBuilder() {

        User follower = new User();
        follower.setUserId(1L);

        User following = new User();
        following.setUserId(2L);

        Follow follow = Follow.builder()
                .followId(50L)
                .follower(follower)
                .following(following)
                .build();

        assertEquals(follower, follow.getFollower());
    }

    @Test
    public void coverShareBuilder() {

        User user = new User();
        user.setUserId(1L);

        Post post = new Post();
        post.setPostId(10L);

        Share share = Share.builder()
                .shareId(1L)
                .sharedBy(user)
                .originalPost(post)
                .build();

        assertEquals(user, share.getSharedBy());
    }

    @Test
    public void coverPostLikeBuilder() {

        User user = new User();
        user.setUserId(1L);

        Post post = new Post();
        post.setPostId(10L);

        PostLike like = PostLike.builder()
                .likeId(1L)
                .user(user)
                .post(post)
                .build();

        assertEquals(post, like.getPost());
    }
}