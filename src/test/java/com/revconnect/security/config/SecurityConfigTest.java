package com.revconnect.security.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class SecurityConfigTest {

    private final SecurityConfig config = new SecurityConfig();

    // Test PasswordEncoder bean creation
    @Test
    void shouldCreatePasswordEncoder() {

        PasswordEncoder encoder = config.passwordEncoder();

        assertNotNull(encoder);
    }

    // Test password encoding functionality
    @Test
    void shouldEncodePasswordCorrectly() {

        PasswordEncoder encoder = config.passwordEncoder();

        String rawPassword = "mypassword";

        String encoded = encoder.encode(rawPassword);

        assertNotEquals(rawPassword, encoded);

        assertTrue(encoder.matches(rawPassword, encoded));
    }
}