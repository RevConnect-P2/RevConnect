package com.revconnect.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UnauthorizedExceptionTest {

    @Test
    void testConstructorAndMessage() {

        UnauthorizedException exception =
                new UnauthorizedException("Access denied");

        assertEquals("Access denied", exception.getMessage());
        assertTrue(exception instanceof RuntimeException);
    }
}