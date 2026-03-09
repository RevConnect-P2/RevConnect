package com.revconnect.security.jwt;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class JwtUtilTest {

    private JwtUtil jwtUtil;

    @Before
    public void setup() {
        jwtUtil = new JwtUtil();
    }

    // -------- generateToken --------

    @Test
    public void shouldGenerateToken() {

        String token = jwtUtil.generateToken("test@mail.com");

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    // -------- extractEmail --------

    @Test
    public void shouldExtractEmailFromToken() {

        String email = "test@mail.com";

        String token = jwtUtil.generateToken(email);

        String extracted = jwtUtil.extractEmail(token);

        assertEquals(email, extracted);
    }

    // -------- validateToken SUCCESS --------

    @Test
    public void shouldValidateTokenSuccessfully() {

        String email = "test@mail.com";

        String token = jwtUtil.generateToken(email);

        boolean valid = jwtUtil.validateToken(token, email);

        assertTrue(valid);
    }

    // -------- validateToken FAIL (wrong email) --------

    @Test
    public void shouldFailValidationIfEmailDifferent() {

        String token = jwtUtil.generateToken("test@mail.com");

        boolean valid = jwtUtil.validateToken(token, "wrong@mail.com");

        assertFalse(valid);
    }

}