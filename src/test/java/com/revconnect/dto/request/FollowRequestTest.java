package com.revconnect.dto.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class FollowRequestTest {

    @Test
    void testConstructor() {

        FollowRequest request = new FollowRequest();

        assertNotNull(request);
    }
}