package com.revconnect.dto.request;

import com.revconnect.enums.ProfileType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProfileCreateRequestTest {

    @Test
    void shouldSetAndGetFieldsCorrectly() {

        ProfileCreateRequest request = new ProfileCreateRequest();

        request.setFullName("John Doe");
        request.setBio("Software Developer");
        request.setProfilePic("profile.jpg");
        request.setLocation("India");
        request.setWebsite("https://example.com");
        request.setProfileVisibility("PUBLIC");
        request.setProfileType(ProfileType.CREATOR);
        request.setCategory("Technology");
        request.setExternalLinks("https://github.com/john");
        request.setBusinessAddress("Bangalore");
        request.setContactInfo("9999999999");

        assertEquals("John Doe", request.getFullName());
        assertEquals("Software Developer", request.getBio());
        assertEquals("profile.jpg", request.getProfilePic());
        assertEquals("India", request.getLocation());
        assertEquals("https://example.com", request.getWebsite());
        assertEquals("PUBLIC", request.getProfileVisibility());
        assertEquals(ProfileType.CREATOR, request.getProfileType());
        assertEquals("Technology", request.getCategory());
        assertEquals("https://github.com/john", request.getExternalLinks());
        assertEquals("Bangalore", request.getBusinessAddress());
        assertEquals("9999999999", request.getContactInfo());
    }
}