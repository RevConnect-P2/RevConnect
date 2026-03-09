package com.revconnect.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void testHandleUnauthorized() {

        UnauthorizedException ex =
                new UnauthorizedException("Access denied");

        ResponseEntity<Map<String, Object>> response =
                handler.handleUnauthorized(ex);

        assertEquals(401, response.getStatusCode().value());
        assertEquals("Access denied", response.getBody().get("message"));
        assertEquals("Unauthorized", response.getBody().get("error"));
    }

    @Test
    void testHandleBadRequest() {

        BadRequestException ex =
                new BadRequestException("Invalid request");

        ResponseEntity<Map<String, Object>> response =
                handler.handleBadRequest(ex);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Invalid request", response.getBody().get("message"));
        assertEquals("Bad Request", response.getBody().get("error"));
    }

    @Test
    void testHandleNotFound() {

        ResourceNotFoundException ex =
                new ResourceNotFoundException("User not found");

        ResponseEntity<Map<String, Object>> response =
                handler.handleNotFound(ex);

        assertEquals(404, response.getStatusCode().value());
        assertEquals("User not found", response.getBody().get("message"));
        assertEquals("Not Found", response.getBody().get("error"));
    }
}