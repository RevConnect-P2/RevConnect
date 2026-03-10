package com.revconnect.service.impl;

import com.revconnect.dto.request.ShowcaseRequest;
import com.revconnect.dto.response.ShowcaseResponse;
import com.revconnect.entity.ProfileShowcase;
import com.revconnect.entity.UserProfile;
import com.revconnect.enums.ProfileType;
import com.revconnect.exception.ResourceNotFoundException;
import com.revconnect.repository.ProfileShowcaseRepository;
import com.revconnect.repository.UserProfileRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ProfileShowcaseServiceImplTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private ProfileShowcaseRepository showcaseRepository;

    @InjectMocks
    private ProfileShowcaseServiceImpl showcaseService;

    private UserProfile profile;
    private ProfileShowcase showcase;

    @Before
    public void setup() {

        profile = new UserProfile();
        profile.setProfileId(1L);
        profile.setProfileType(ProfileType.CREATOR);

        showcase = new ProfileShowcase();
        showcase.setShowcaseId(10L);
        showcase.setProfile(profile);
        showcase.setTitle("Sample");
    }

    // -------- addShowcase --------

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowIfProfileNotFound() {

        when(userProfileRepository.findByUser_UserId(1L))
                .thenReturn(Optional.empty());

        showcaseService.addShowcase(1L, new ShowcaseRequest());
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowIfPersonalProfile() {

        profile.setProfileType(ProfileType.PERSONAL);

        when(userProfileRepository.findByUser_UserId(1L))
                .thenReturn(Optional.of(profile));

        showcaseService.addShowcase(1L, new ShowcaseRequest());
    }

    @Test
    public void shouldAddShowcaseSuccessfully() {

        when(userProfileRepository.findByUser_UserId(1L))
                .thenReturn(Optional.of(profile));

        showcaseService.addShowcase(1L, new ShowcaseRequest());

        verify(showcaseRepository).save(any(ProfileShowcase.class));
    }

    // -------- getShowcases --------

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowIfProfileNotFoundForGet() {

        when(userProfileRepository.findByUser_UserId(1L))
                .thenReturn(Optional.empty());

        showcaseService.getShowcases(1L);
    }

    @Test
    public void shouldReturnShowcases() {

        when(userProfileRepository.findByUser_UserId(1L))
                .thenReturn(Optional.of(profile));

        when(showcaseRepository.findByProfile_ProfileId(1L))
                .thenReturn(List.of(showcase));

        List<ShowcaseResponse> result =
                showcaseService.getShowcases(1L);

        assertEquals(1, result.size());
    }

    // -------- updateShowcase --------

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowIfProfileNotFoundForUpdate() {

        when(userProfileRepository.findByUser_UserId(1L))
                .thenReturn(Optional.empty());

        showcaseService.updateShowcase(1L,10L,new ShowcaseRequest());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowIfShowcaseNotFound() {

        when(userProfileRepository.findByUser_UserId(1L))
                .thenReturn(Optional.of(profile));

        when(showcaseRepository
                .findByShowcaseIdAndProfile_ProfileId(10L,1L))
                .thenReturn(Optional.empty());

        showcaseService.updateShowcase(1L,10L,new ShowcaseRequest());
    }

    @Test
    public void shouldUpdateShowcaseSuccessfully() {

        when(userProfileRepository.findByUser_UserId(1L))
                .thenReturn(Optional.of(profile));

        when(showcaseRepository
                .findByShowcaseIdAndProfile_ProfileId(10L,1L))
                .thenReturn(Optional.of(showcase));

        showcaseService.updateShowcase(1L,10L,new ShowcaseRequest());

        verify(showcaseRepository).save(showcase);
    }

    // -------- deleteShowcase --------

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowIfProfileNotFoundForDelete() {

        when(userProfileRepository.findByUser_UserId(1L))
                .thenReturn(Optional.empty());

        showcaseService.deleteShowcase(1L,10L);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowIfShowcaseNotFoundForDelete() {

        when(userProfileRepository.findByUser_UserId(1L))
                .thenReturn(Optional.of(profile));

        when(showcaseRepository
                .findByShowcaseIdAndProfile_ProfileId(10L,1L))
                .thenReturn(Optional.empty());

        showcaseService.deleteShowcase(1L,10L);
    }

    @Test
    public void shouldDeleteShowcaseSuccessfully() {

        when(userProfileRepository.findByUser_UserId(1L))
                .thenReturn(Optional.of(profile));

        when(showcaseRepository
                .findByShowcaseIdAndProfile_ProfileId(10L,1L))
                .thenReturn(Optional.of(showcase));

        showcaseService.deleteShowcase(1L,10L);

        verify(showcaseRepository).delete(showcase);
    }
}