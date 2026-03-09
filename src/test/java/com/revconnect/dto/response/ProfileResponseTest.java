package com.revconnect.dto.response;

import com.revconnect.enums.ProfileType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProfileResponseTest {

    @Test
    void shouldSetAndGetFieldsCorrectly() {

        ProfileResponse response = new ProfileResponse();

        response.setUserId(1L);
        response.setUsername("john");
        response.setFullName("John Doe");
        response.setBio("Developer");
        response.setProfilePic("pic.jpg");
        response.setLocation("India");
        response.setWebsite("https://example.com");
        response.setProfileVisibility("PUBLIC");
        response.setProfileType(ProfileType.CREATOR);
        response.setCategory("Technology");
        response.setExternalLinks("https://github.com/john");
        response.setBusinessAddress("Bangalore");
        response.setContactInfo("9999999999");

        assertEquals(1L, response.getUserId());
        assertEquals("john", response.getUsername());
        assertEquals("John Doe", response.getFullName());
        assertEquals("Developer", response.getBio());
        assertEquals("pic.jpg", response.getProfilePic());
        assertEquals("India", response.getLocation());
        assertEquals("https://example.com", response.getWebsite());
        assertEquals("PUBLIC", response.getProfileVisibility());
        assertEquals(ProfileType.CREATOR, response.getProfileType());
        assertEquals("Technology", response.getCategory());
        assertEquals("https://github.com/john", response.getExternalLinks());
        assertEquals("Bangalore", response.getBusinessAddress());
        assertEquals("9999999999", response.getContactInfo());
    }
}