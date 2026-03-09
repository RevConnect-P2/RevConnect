package com.revconnect.entity;

import com.revconnect.enums.ProfileType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserProfileTest {

    @Test
    void shouldSetAndGetFieldsCorrectly() {

        User user = new User();
        user.setUserId(1L);

        UserProfile profile = new UserProfile();

        profile.setProfileId(10L);
        profile.setUser(user);
        profile.setFullName("John Doe");
        profile.setBio("Software developer");
        profile.setLocation("India");
        profile.setWebsite("example.com");

        assertEquals(10L, profile.getProfileId());
        assertEquals(user, profile.getUser());
        assertEquals("John Doe", profile.getFullName());
        assertEquals("Software developer", profile.getBio());
        assertEquals("India", profile.getLocation());
        assertEquals("example.com", profile.getWebsite());
    }

    @Test
    void shouldBuildProfileUsingBuilder() {

        User user = new User();
        user.setUserId(1L);

        UserProfile profile = UserProfile.builder()
                .profileId(5L)
                .user(user)
                .fullName("Alice")
                .profileType(ProfileType.CREATOR)
                .build();

        assertEquals(5L, profile.getProfileId());
        assertEquals(user, profile.getUser());
        assertEquals("Alice", profile.getFullName());
        assertEquals(ProfileType.CREATOR, profile.getProfileType());
    }

    @Test
    void shouldSetCreatedAtAndDefaultProfileTypeOnCreate() {

        User user = new User();
        user.setUserId(1L);

        UserProfile profile = new UserProfile();
        profile.setUser(user);

        profile.onCreate();

        assertNotNull(profile.getCreatedAt());
        assertEquals(ProfileType.PERSONAL, profile.getProfileType());
    }

    @Test
    void shouldSetUpdatedAtOnUpdate() {

        User user = new User();
        user.setUserId(1L);

        UserProfile profile = new UserProfile();
        profile.setUser(user);
        profile.setProfileType(ProfileType.BUSINESS);

        profile.onUpdate();

        assertNotNull(profile.getUpdatedAt());
    }

}