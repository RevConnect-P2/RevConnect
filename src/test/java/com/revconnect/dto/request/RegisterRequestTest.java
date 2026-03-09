package com.revconnect.dto.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegisterRequestTest {

    @Test
    void testNoArgsConstructorAndSetters() {

        RegisterRequest request = new RegisterRequest();

        request.setEmail("user@mail.com");
        request.setUsername("john");
        request.setPassword("password123");
        request.setUserType("CREATOR");
        request.setSecurityQuestion("Your first pet?");
        request.setSecurityAnswer("Tommy");

        assertEquals("user@mail.com", request.getEmail());
        assertEquals("john", request.getUsername());
        assertEquals("password123", request.getPassword());
        assertEquals("CREATOR", request.getUserType());
        assertEquals("Your first pet?", request.getSecurityQuestion());
        assertEquals("Tommy", request.getSecurityAnswer());
    }

    @Test
    void testAllArgsConstructor() {

        RegisterRequest request =
                new RegisterRequest(
                        "test@mail.com",
                        "dhanush",
                        "secret",
                        "BUSINESS",
                        "Favorite color?",
                        "Blue"
                );

        assertEquals("test@mail.com", request.getEmail());
        assertEquals("dhanush", request.getUsername());
        assertEquals("secret", request.getPassword());
        assertEquals("BUSINESS", request.getUserType());
        assertEquals("Favorite color?", request.getSecurityQuestion());
        assertEquals("Blue", request.getSecurityAnswer());
    }

    @Test
    void testBuilder() {

        RegisterRequest request = RegisterRequest.builder()
                .email("builder@mail.com")
                .username("builderUser")
                .password("builderPass")
                .userType("PERSONAL")
                .securityQuestion("City you were born?")
                .securityAnswer("Mangalore")
                .build();

        assertEquals("builder@mail.com", request.getEmail());
        assertEquals("builderUser", request.getUsername());
        assertEquals("builderPass", request.getPassword());
        assertEquals("PERSONAL", request.getUserType());
        assertEquals("City you were born?", request.getSecurityQuestion());
        assertEquals("Mangalore", request.getSecurityAnswer());
    }
}