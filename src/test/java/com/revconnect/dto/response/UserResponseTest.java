package com.revconnect.dto.response;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserResponseTest {

    @Test
    void testConstructor() {

        UserResponse response = new UserResponse();

        assertNotNull(response);
    }
}