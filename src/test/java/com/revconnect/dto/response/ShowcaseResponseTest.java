package com.revconnect.dto.response;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShowcaseResponseTest {

    @Test
    void testSettersAndGetters() {

        ShowcaseResponse response = new ShowcaseResponse();

        response.setShowcaseId(1L);
        response.setTitle("Logo Design");
        response.setDescription("Professional logo design");
        response.setPrice(150.0);
        response.setImageUrl("https://example.com/logo.png");

        assertEquals(1L, response.getShowcaseId());
        assertEquals("Logo Design", response.getTitle());
        assertEquals("Professional logo design", response.getDescription());
        assertEquals(150.0, response.getPrice());
        assertEquals("https://example.com/logo.png", response.getImageUrl());
    }

    @Test
    void testAllArgsConstructor() {

        ShowcaseResponse response =
                new ShowcaseResponse(
                        2L,
                        "Web Development",
                        "Full stack website",
                        999.0,
                        "https://example.com/web.png"
                );

        assertEquals(2L, response.getShowcaseId());
        assertEquals("Web Development", response.getTitle());
        assertEquals("Full stack website", response.getDescription());
        assertEquals(999.0, response.getPrice());
        assertEquals("https://example.com/web.png", response.getImageUrl());
    }
}