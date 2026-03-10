package com.revconnect.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BadRequestExceptionTest {

    @Test
    void testConstructorAndMessage() {

        BadRequestException exception =
                new BadRequestException("Invalid request");

        assertEquals("Invalid request", exception.getMessage());
        assertTrue(exception instanceof RuntimeException);
    }
}