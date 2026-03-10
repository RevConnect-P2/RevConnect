package com.revconnect.dto.response;

import com.revconnect.enums.ProfileType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProfileResponseTest {

    @Test
    void shouldTestGettersAndSetters() {

        ProfileResponse response = new ProfileResponse();

        response.setUserId(1L);
        response.setUsername("john123");
        response.setFullName("John Doe");
        response.setBio("Developer");
        response.setProfilePic("pic.png");
        response.setLocation("India");
        response.setWebsite("https://example.com");
        response.setProfileVisibility("PUBLIC");

        response.setProfileType(ProfileType.CREATOR);
        response.setCategory("Tech");
        response.setExternalLinks("github.com/john");
        response.setBusinessAddress("Bangalore");
        response.setContactInfo("9876543210");

        assertEquals(1L, response.getUserId());
        assertEquals("john123", response.getUsername());
        assertEquals("John Doe", response.getFullName());
        assertEquals("Developer", response.getBio());
        assertEquals("pic.png", response.getProfilePic());
        assertEquals("India", response.getLocation());
        assertEquals("https://example.com", response.getWebsite());
        assertEquals("PUBLIC", response.getProfileVisibility());

        assertEquals(ProfileType.CREATOR, response.getProfileType());
        assertEquals("Tech", response.getCategory());
        assertEquals("github.com/john", response.getExternalLinks());
        assertEquals("Bangalore", response.getBusinessAddress());
        assertEquals("9876543210", response.getContactInfo());
    }

    @Test
    void shouldTestEqualsAndHashCode() {

        ProfileResponse r1 = new ProfileResponse();
        r1.setUserId(1L);

        ProfileResponse r2 = new ProfileResponse();
        r2.setUserId(1L);

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void shouldTestToString() {

        ProfileResponse response = new ProfileResponse();
        response.setUsername("tester");

        String result = response.toString();

        assertTrue(result.contains("tester"));
    }
}