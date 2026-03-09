package com.revconnect.dto.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShowcaseRequestTest {

    @Test
    void testGettersAndSetters() {

        ShowcaseRequest request = new ShowcaseRequest();

        request.setTitle("Logo Design");
        request.setDescription("Professional logo design service");
        request.setPrice(199.99);
        request.setImageUrl("https://example.com/logo.png");

        assertEquals("Logo Design", request.getTitle());
        assertEquals("Professional logo design service", request.getDescription());
        assertEquals(199.99, request.getPrice());
        assertEquals("https://example.com/logo.png", request.getImageUrl());
    }
}