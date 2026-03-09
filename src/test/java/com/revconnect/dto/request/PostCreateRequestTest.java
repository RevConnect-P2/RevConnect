package com.revconnect.dto.request;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PostCreateRequestTest {

    @Test
    void testSettersAndGetters() {

        PostCreateRequest request = new PostCreateRequest();

        LocalDateTime schedule = LocalDateTime.now();

        request.setContent("Sample post content");
        request.setPostType("NORMAL");
        request.setPinned(true);
        request.setCtaText("Buy Now");
        request.setCtaLink("https://example.com");
        request.setScheduledAt(schedule);
        request.setHashtags(List.of("#java", "#spring"));
        request.setTags(List.of());

        assertEquals("Sample post content", request.getContent());
        assertEquals("NORMAL", request.getPostType());
        assertTrue(request.getPinned());
        assertEquals("Buy Now", request.getCtaText());
        assertEquals("https://example.com", request.getCtaLink());
        assertEquals(schedule, request.getScheduledAt());
        assertEquals(2, request.getHashtags().size());
        assertNotNull(request.getTags());
    }

    @Test
    void testAllArgsConstructor() {

        LocalDateTime schedule = LocalDateTime.now();

        PostCreateRequest request =
                new PostCreateRequest(
                        "Content",
                        "PROMOTIONAL",
                        false,
                        "Learn More",
                        "https://revconnect.com",
                        schedule,
                        List.of("#marketing"),
                        List.of()
                );

        assertEquals("Content", request.getContent());
        assertEquals("PROMOTIONAL", request.getPostType());
        assertFalse(request.getPinned());
        assertEquals("Learn More", request.getCtaText());
        assertEquals("https://revconnect.com", request.getCtaLink());
        assertEquals(schedule, request.getScheduledAt());
        assertEquals(1, request.getHashtags().size());
    }

    @Test
    void testBuilder() {

        PostCreateRequest request = PostCreateRequest.builder()
                .content("Builder post")
                .postType("NORMAL")
                .pinned(true)
                .ctaText("Click")
                .ctaLink("https://builder.com")
                .hashtags(List.of("#builder"))
                .tags(List.of())
                .build();

        assertEquals("Builder post", request.getContent());
        assertEquals("NORMAL", request.getPostType());
        assertTrue(request.getPinned());
        assertEquals("Click", request.getCtaText());
        assertEquals("https://builder.com", request.getCtaLink());
        assertEquals(1, request.getHashtags().size());
    }
}