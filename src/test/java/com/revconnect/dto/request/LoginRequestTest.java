package com.revconnect.dto.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginRequestTest {

    @Test
    void testNoArgsConstructorAndSetters() {

        LoginRequest request = new LoginRequest();

        request.setEmail("test@mail.com");
        request.setPassword("password123");

        assertEquals("test@mail.com", request.getEmail());
        assertEquals("password123", request.getPassword());
    }

    @Test
    void testAllArgsConstructor() {

        LoginRequest request =
                new LoginRequest("user@mail.com", "secret");

        assertEquals("user@mail.com", request.getEmail());
        assertEquals("secret", request.getPassword());
    }

    @Test
    void testBuilder() {

        LoginRequest request = LoginRequest.builder()
                .email("builder@mail.com")
                .password("builderPass")
                .build();

        assertEquals("builder@mail.com", request.getEmail());
        assertEquals("builderPass", request.getPassword());
    }
}