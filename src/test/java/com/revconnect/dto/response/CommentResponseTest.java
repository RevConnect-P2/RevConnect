package com.revconnect.dto.response;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CommentResponseTest {

    @Test
    void testBuilderAndGetters() {

        LocalDateTime time = LocalDateTime.now();

        CommentResponse response = CommentResponse.builder()
                .commentId(1L)
                .commentText("Nice post!")
                .createdAt(time)
                .username("john")
                .build();

        assertEquals(1L, response.getCommentId());
        assertEquals("Nice post!", response.getCommentText());
        assertEquals(time, response.getCreatedAt());
        assertEquals("john", response.getUsername());
    }
}