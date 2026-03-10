package com.revconnect.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PostHashtagTest {

    @Test
    void shouldTestGettersAndSetters() {

        PostHashtag postHashtag = new PostHashtag();

        Post post = new Post();
        Hashtag hashtag = new Hashtag();

        LocalDateTime time = LocalDateTime.now();

        postHashtag.setId(1L);
        postHashtag.setPost(post);
        postHashtag.setHashtag(hashtag);
        postHashtag.setTaggedAt(time);

        assertEquals(1L, postHashtag.getId());
        assertEquals(post, postHashtag.getPost());
        assertEquals(hashtag, postHashtag.getHashtag());
        assertEquals(time, postHashtag.getTaggedAt());
    }

    @Test
    void shouldTestBuilder() {

        Post post = new Post();
        Hashtag hashtag = new Hashtag();

        PostHashtag postHashtag = PostHashtag.builder()
                .id(2L)
                .post(post)
                .hashtag(hashtag)
                .taggedAt(LocalDateTime.now())
                .build();

        assertEquals(2L, postHashtag.getId());
        assertEquals(post, postHashtag.getPost());
        assertEquals(hashtag, postHashtag.getHashtag());
        assertNotNull(postHashtag.getTaggedAt());
    }

    @Test
    void shouldTestAllArgsConstructor() {

        Post post = new Post();
        Hashtag hashtag = new Hashtag();
        LocalDateTime time = LocalDateTime.now();

        PostHashtag postHashtag = new PostHashtag(
                3L,
                post,
                hashtag,
                time
        );

        assertEquals(3L, postHashtag.getId());
        assertEquals(post, postHashtag.getPost());
        assertEquals(hashtag, postHashtag.getHashtag());
        assertEquals(time, postHashtag.getTaggedAt());
    }

    @Test
    void shouldSetTaggedAtOnCreate() {

        PostHashtag postHashtag = new PostHashtag();

        postHashtag.onCreate();

        assertNotNull(postHashtag.getTaggedAt());
    }
}