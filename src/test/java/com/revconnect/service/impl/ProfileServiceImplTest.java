package com.revconnect.service.impl;

import com.revconnect.dto.request.ProfileCreateRequest;
import com.revconnect.dto.request.ProfileUpdateRequest;
import com.revconnect.dto.request.BusinessHoursRequest;
import com.revconnect.entity.User;
import com.revconnect.entity.UserProfile;
import com.revconnect.entity.BusinessHours;
import com.revconnect.enums.ProfileType;
import com.revconnect.exception.ResourceNotFoundException;
import com.revconnect.repository.UserRepository;
import com.revconnect.repository.UserProfileRepository;
import com.revconnect.repository.BusinessHoursRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ProfileServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private BusinessHoursRepository businessHoursRepository;

    @InjectMocks
    private ProfileServiceImpl profileService;

    private User user;
    private UserProfile profile;

    @Before
    public void setup() {

        user = new User();
        user.setUserId(1L);
        user.setUsername("john");
        user.setUserType("PERSONAL");

        profile = new UserProfile();
        profile.setProfileId(10L);
        profile.setUser(user);
        profile.setProfileType(ProfileType.PERSONAL);
    }

    // -------- CREATE PROFILE --------

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowIfUserNotFound() {

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        profileService.createProfile(1L, new ProfileCreateRequest());
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowIfProfileAlreadyExists() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userProfileRepository.findByUser_UserId(1L))
                .thenReturn(Optional.of(profile));

        profileService.createProfile(1L, new ProfileCreateRequest());
    }

    @Test
    public void shouldCreateProfileSuccessfully() {

        ProfileCreateRequest req = new ProfileCreateRequest();
        req.setFullName("John Doe");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userProfileRepository.findByUser_UserId(1L))
                .thenReturn(Optional.empty());

        when(userProfileRepository.save(any(UserProfile.class)))
                .thenReturn(profile);

        profileService.createProfile(1L, req);

        verify(userProfileRepository).save(any(UserProfile.class));
    }

    // -------- UPDATE PROFILE --------

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowIfUpdateProfileNotFound() {

        when(userProfileRepository.findByUser_UserId(1L))
                .thenReturn(Optional.empty());

        profileService.updateProfile(1L, new ProfileUpdateRequest());
    }

    @Test
    public void shouldUpdateProfileSuccessfully() {

        when(userProfileRepository.findByUser_UserId(1L))
                .thenReturn(Optional.of(profile));

        when(userProfileRepository.save(any(UserProfile.class)))
                .thenReturn(profile);

        profileService.updateProfile(1L, new ProfileUpdateRequest());

        verify(userProfileRepository).save(profile);
    }

    // -------- GET PROFILE --------

    @Test
    public void shouldReturnExistingProfile() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userProfileRepository.findByUser_UserId(1L))
                .thenReturn(Optional.of(profile));

        assertNotNull(profileService.getProfile(1L));
    }

    @Test
    public void shouldCreateProfileIfMissing() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userProfileRepository.findByUser_UserId(1L))
                .thenReturn(Optional.empty());

        when(userProfileRepository.save(any(UserProfile.class)))
                .thenReturn(profile);

        assertNotNull(profileService.getProfile(1L));
    }

    // -------- SEARCH --------

    @Test
    public void shouldSearchProfiles() {

        profile.setProfileVisibility("PUBLIC");

        when(userProfileRepository
                .findByUser_UsernameContainingIgnoreCaseOrFullNameContainingIgnoreCase("jo","jo"))
                .thenReturn(List.of(profile));

        assertEquals(1, profileService.searchProfiles("jo").size());
    }

    // -------- BUSINESS HOURS --------

    @Test(expected = IllegalStateException.class)
    public void shouldThrowIfNotBusinessProfile() {

        when(userProfileRepository.findByUser_UserId(1L))
                .thenReturn(Optional.of(profile));

        profileService.addBusinessHours(1L, List.of());
    }

    @Test
    public void shouldAddBusinessHours() {

        profile.setProfileType(ProfileType.BUSINESS);

        BusinessHoursRequest req = new BusinessHoursRequest();
        req.setDayOfWeek("MONDAY");
        req.setOpenTime(LocalTime.of(9,0));
        req.setCloseTime(LocalTime.of(18,0));
        req.setIsClosed(false);

        when(userProfileRepository.findByUser_UserId(1L))
                .thenReturn(Optional.of(profile));

        profileService.addBusinessHours(1L, List.of(req));

        verify(businessHoursRepository).save(any(BusinessHours.class));
    }

    // -------- DELETE ALL HOURS --------

    @Test
    public void shouldDeleteAllBusinessHours() {

        profile.setProfileType(ProfileType.BUSINESS);

        when(userProfileRepository.findByUser_UserId(1L))
                .thenReturn(Optional.of(profile));

        profileService.deleteAllBusinessHours(1L);

        verify(businessHoursRepository)
                .deleteByProfile_ProfileId(profile.getProfileId());
    }

    @Test
    public void shouldUpdateBusinessHours() {

        profile.setProfileType(ProfileType.BUSINESS);

        BusinessHours hours = new BusinessHours();
        hours.setDayOfWeek("MONDAY");

        BusinessHoursRequest req = new BusinessHoursRequest();
        req.setOpenTime(LocalTime.of(9,0));
        req.setCloseTime(LocalTime.of(17,0));
        req.setIsClosed(false);

        when(userProfileRepository.findByUser_UserId(1L))
                .thenReturn(Optional.of(profile));

        when(businessHoursRepository.findByProfile_ProfileId(profile.getProfileId()))
                .thenReturn(List.of(hours));

        profileService.updateBusinessHours(1L,"MONDAY",req);

        verify(businessHoursRepository).save(hours);
    }

    @Test
    public void shouldDeleteBusinessHours() {

        profile.setProfileType(ProfileType.BUSINESS);

        BusinessHours hours = new BusinessHours();
        hours.setDayOfWeek("MONDAY");

        when(userProfileRepository.findByUser_UserId(1L))
                .thenReturn(Optional.of(profile));

        when(businessHoursRepository.findByProfile_ProfileId(profile.getProfileId()))
                .thenReturn(List.of(hours));

        profileService.deleteBusinessHours(1L,"MONDAY");

        verify(businessHoursRepository).delete(hours);
    }

    @Test
    public void shouldReturnSortedBusinessHours() {

        profile.setProfileType(ProfileType.BUSINESS);

        BusinessHours h1 = new BusinessHours();
        h1.setDayOfWeek("TUESDAY");

        BusinessHours h2 = new BusinessHours();
        h2.setDayOfWeek("MONDAY");

        when(userProfileRepository.findByUser_UserId(1L))
                .thenReturn(Optional.of(profile));

        when(businessHoursRepository.findByProfile_ProfileId(profile.getProfileId()))
                .thenReturn(List.of(h1,h2));

        List<?> result = profileService.getBusinessHours(1L);

        assertEquals(2,result.size());
    }

    @Test
    public void shouldFilterPrivateProfiles() {

        UserProfile privateProfile = new UserProfile();
        privateProfile.setUser(user);
        privateProfile.setProfileVisibility("PRIVATE");

        when(userProfileRepository
                .findByUser_UsernameContainingIgnoreCaseOrFullNameContainingIgnoreCase("jo","jo"))
                .thenReturn(List.of(privateProfile));

        assertEquals(0, profileService.searchProfiles("jo").size());
    }


}