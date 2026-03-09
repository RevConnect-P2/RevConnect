package com.revconnect.dto.response;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class PageResponseTest {

    @Test
    void testConstructor() {

        PageResponse response = new PageResponse();

        assertNotNull(response);
    }
}