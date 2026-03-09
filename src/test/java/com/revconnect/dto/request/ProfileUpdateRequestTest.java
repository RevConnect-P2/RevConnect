package com.revconnect.dto.request;

import com.revconnect.enums.ProfileType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProfileUpdateRequestTest {

    @Test
    void testSettersAndGetters() {

        ProfileUpdateRequest request = new ProfileUpdateRequest();

        request.setFullName("John Doe");
        request.setBio("Software Developer");
        request.setProfilePic("profile.jpg");
        request.setLocation("India");
        request.setWebsite("https://example.com");
        request.setProfileVisibility("PUBLIC");
        request.setProfileType(ProfileType.PERSONAL);
        request.setCategory("Tech");
        request.setExternalLinks("https://linkedin.com");
        request.setBusinessAddress("Bangalore");
        request.setContactInfo("9999999999");

        assertEquals("John Doe", request.getFullName());
        assertEquals("Software Developer", request.getBio());
        assertEquals("profile.jpg", request.getProfilePic());
        assertEquals("India", request.getLocation());
        assertEquals("https://example.com", request.getWebsite());
        assertEquals("PUBLIC", request.getProfileVisibility());
        assertEquals(ProfileType.PERSONAL, request.getProfileType());
        assertEquals("Tech", request.getCategory());
        assertEquals("https://linkedin.com", request.getExternalLinks());
        assertEquals("Bangalore", request.getBusinessAddress());
        assertEquals("9999999999", request.getContactInfo());
    }

    @Test
    void testEqualsAndHashCode() {

        ProfileUpdateRequest req1 = new ProfileUpdateRequest();
        req1.setFullName("John");

        ProfileUpdateRequest req2 = new ProfileUpdateRequest();
        req2.setFullName("John");

        assertEquals(req1, req2);
        assertEquals(req1.hashCode(), req2.hashCode());
    }

    @Test
    void testToString() {

        ProfileUpdateRequest request = new ProfileUpdateRequest();
        request.setFullName("John");

        String result = request.toString();

        assertNotNull(result);
        assertTrue(result.contains("John"));
    }
}