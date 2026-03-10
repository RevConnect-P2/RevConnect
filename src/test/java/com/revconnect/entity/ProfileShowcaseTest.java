package com.revconnect.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ProfileShowcaseTest {

    @Test
    void shouldSetAndGetFieldsCorrectly() {

        UserProfile profile = new UserProfile();
        profile.setProfileId(1L);

        ProfileShowcase showcase = new ProfileShowcase();

        showcase.setShowcaseId(10L);
        showcase.setProfile(profile);
        showcase.setTitle("Logo Design");
        showcase.setDescription("Professional logo design service");
        showcase.setPrice(500.0);
        showcase.setImageUrl("image.png");

        assertEquals(10L, showcase.getShowcaseId());
        assertEquals(profile, showcase.getProfile());
        assertEquals("Logo Design", showcase.getTitle());
        assertEquals("Professional logo design service", showcase.getDescription());
        assertEquals(500.0, showcase.getPrice());
        assertEquals("image.png", showcase.getImageUrl());
    }

    @Test
    void shouldBuildShowcaseUsingBuilder() {

        UserProfile profile = new UserProfile();
        profile.setProfileId(1L);

        ProfileShowcase showcase = ProfileShowcase.builder()
                .showcaseId(5L)
                .profile(profile)
                .title("Website Development")
                .description("Full stack web development")
                .price(2000.0)
                .imageUrl("site.png")
                .build();

        assertEquals(5L, showcase.getShowcaseId());
        assertEquals(profile, showcase.getProfile());
        assertEquals("Website Development", showcase.getTitle());
        assertEquals(2000.0, showcase.getPrice());
    }

    @Test
    void shouldSetCreatedAtOnPrePersist() {

        UserProfile profile = new UserProfile();
        profile.setProfileId(1L);

        ProfileShowcase showcase = new ProfileShowcase();
        showcase.setProfile(profile);
        showcase.setTitle("Graphic Design");

        showcase.onCreate();

        assertNotNull(showcase.getCreatedAt());
    }
}