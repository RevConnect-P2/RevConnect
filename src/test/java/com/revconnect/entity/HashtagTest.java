package com.revconnect.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class HashtagTest {

    @Test
    void shouldTestGettersAndSetters() {

        Hashtag hashtag = new Hashtag();

        LocalDateTime time = LocalDateTime.now();

        hashtag.setHashtagId(1L);
        hashtag.setTagName("java");
        hashtag.setCreatedAt(time);

        assertEquals(1L, hashtag.getHashtagId());
        assertEquals("java", hashtag.getTagName());
        assertEquals(time, hashtag.getCreatedAt());
    }

    @Test
    void shouldTestBuilder() {

        Hashtag hashtag = Hashtag.builder()
                .hashtagId(2L)
                .tagName("springboot")
                .createdAt(LocalDateTime.now())
                .build();

        assertEquals(2L, hashtag.getHashtagId());
        assertEquals("springboot", hashtag.getTagName());
        assertNotNull(hashtag.getCreatedAt());
    }

    @Test
    void shouldTestAllArgsConstructor() {

        LocalDateTime time = LocalDateTime.now();

        Hashtag hashtag = new Hashtag(
                3L,
                "testing",
                time
        );

        assertEquals(3L, hashtag.getHashtagId());
        assertEquals("testing", hashtag.getTagName());
        assertEquals(time, hashtag.getCreatedAt());
    }

    @Test
    void shouldSetCreatedAtOnCreate() {

        Hashtag hashtag = new Hashtag();

        hashtag.onCreate();

        assertNotNull(hashtag.getCreatedAt());
    }
}