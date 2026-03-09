package com.revconnect.dto.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class CommentRequestTest {

    @Test
    void testConstructor() {

        CommentRequest request = new CommentRequest();

        assertNotNull(request);
    }
}