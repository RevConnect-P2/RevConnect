package com.revconnect.dto.request;

import com.revconnect.enums.ProfileType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProfileCreateRequestTest {

    @Test
    void shouldTestGettersAndSetters() {

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
        request.setContactInfo("9876543210");

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
        assertEquals("9876543210", request.getContactInfo());
    }

    @Test
    void shouldTestEqualsAndHashCode() {

        ProfileCreateRequest r1 = new ProfileCreateRequest();
        r1.setFullName("Test");

        ProfileCreateRequest r2 = new ProfileCreateRequest();
        r2.setFullName("Test");

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void shouldTestToString() {

        ProfileCreateRequest request = new ProfileCreateRequest();
        request.setFullName("Tester");

        String result = request.toString();

        assertTrue(result.contains("Tester"));
    }
}