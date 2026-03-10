package com.revconnect.entity;

import org.junit.Test;
import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

public class EntityLifecycleTest {

    @Test
    public void testUserAnalyticsLifecycle() throws Exception {

        User user = new User();
        user.setUserId(1L);

        UserAnalytics analytics = new UserAnalytics();
        analytics.setUser(user);

        Method prePersist = UserAnalytics.class.getDeclaredMethod("onCreate");
        prePersist.setAccessible(true);
        prePersist.invoke(analytics);

        assertNotNull(analytics.getCreatedAt());

        Method preUpdate = UserAnalytics.class.getDeclaredMethod("onUpdate");
        preUpdate.setAccessible(true);
        preUpdate.invoke(analytics);

        assertNotNull(analytics.getUpdatedAt());
    }

    @Test
    public void testPostAnalyticsLifecycle() throws Exception {

        Post post = new Post();
        post.setPostId(10L);

        PostAnalytics analytics = new PostAnalytics();
        analytics.setPost(post);

        Method prePersist = PostAnalytics.class.getDeclaredMethod("onCreate");
        prePersist.setAccessible(true);
        prePersist.invoke(analytics);

        assertNotNull(analytics.getCreatedAt());

        Method preUpdate = PostAnalytics.class.getDeclaredMethod("onUpdate");
        preUpdate.setAccessible(true);
        preUpdate.invoke(analytics);

        assertNotNull(analytics.getUpdatedAt());
    }

    @Test
    public void testHashtagLifecycle() throws Exception {

        Hashtag hashtag = new Hashtag();
        hashtag.setTagName("java");

        Method prePersist = Hashtag.class.getDeclaredMethod("onCreate");
        prePersist.setAccessible(true);
        prePersist.invoke(hashtag);

        assertNotNull(hashtag.getCreatedAt());
    }

    @Test
    public void testPostHashtagLifecycle() throws Exception {

        Post post = new Post();
        post.setPostId(1L);

        Hashtag hashtag = new Hashtag();
        hashtag.setTagName("spring");

        PostHashtag ph = new PostHashtag();
        ph.setPost(post);
        ph.setHashtag(hashtag);

        Method prePersist = PostHashtag.class.getDeclaredMethod("onCreate");
        prePersist.setAccessible(true);
        prePersist.invoke(ph);

        assertNotNull(ph.getTaggedAt());
    }
}