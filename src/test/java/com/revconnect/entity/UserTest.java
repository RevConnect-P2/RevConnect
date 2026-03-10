package com.revconnect.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void shouldSetAndGetFieldsCorrectly() {

        User user = new User();

        user.setUserId(1L);
        user.setEmail("john@example.com");
        user.setUsername("john");
        user.setPassword("password123");
        user.setUserType("CREATOR");
        user.setIsPrivate(true);
        user.setSecurityQuestion("Pet name?");
        user.setSecurityAnswer("Tom");

        assertEquals(1L, user.getUserId());
        assertEquals("john@example.com", user.getEmail());
        assertEquals("john", user.getUsername());
        assertEquals("password123", user.getPassword());
        assertEquals("CREATOR", user.getUserType());
        assertTrue(user.getIsPrivate());
        assertEquals("Pet name?", user.getSecurityQuestion());
        assertEquals("Tom", user.getSecurityAnswer());
    }

    @Test
    void shouldBuildUserUsingBuilder() {

        User user = User.builder()
                .userId(1L)
                .email("john@example.com")
                .username("john")
                .password("password123")
                .userType("CREATOR")
                .isPrivate(false)
                .build();

        assertEquals("john@example.com", user.getEmail());
        assertEquals("john", user.getUsername());
        assertFalse(user.getIsPrivate());
    }

    @Test
    void shouldSetCreatedAtOnPrePersist() {

        User user = new User();

        user.prePersist();

        assertNotNull(user.getCreatedAt());
    }

    @Test
    void shouldSetUpdatedAtOnPreUpdate() {

        User user = new User();

        user.preUpdate();

        assertNotNull(user.getUpdatedAt());
    }
}