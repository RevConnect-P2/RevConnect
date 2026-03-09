package com.revconnect.service.impl;

import com.revconnect.dto.request.BusinessHoursRequest;
import com.revconnect.entity.UserProfile;
import com.revconnect.enums.ProfileType;
import com.revconnect.exception.ResourceNotFoundException;
import com.revconnect.repository.BusinessHoursRepository;
import com.revconnect.repository.UserProfileRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class BusinessHoursServiceImplTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private BusinessHoursRepository businessHoursRepository;

    @InjectMocks
    private BusinessHoursServiceImpl businessHoursService;

    private UserProfile profile;

    @Before
    public void setup() {

        profile = new UserProfile();
        profile.setProfileId(1L);
        profile.setProfileType(ProfileType.BUSINESS);
    }

    // -------- Profile Not Found --------

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowIfProfileNotFound() {

        when(userProfileRepository.findByUser_UserId(1L))
                .thenReturn(Optional.empty());

        businessHoursService.addBusinessHours(1L, List.of());
    }

    // -------- Not BUSINESS Profile --------

    @Test(expected = IllegalStateException.class)
    public void shouldThrowIfNotBusinessProfile() {

        profile.setProfileType(ProfileType.PERSONAL);

        when(userProfileRepository.findByUser_UserId(1L))
                .thenReturn(Optional.of(profile));

        businessHoursService.addBusinessHours(1L, List.of());
    }

    // -------- Success Case --------

    @Test
    public void shouldAddBusinessHoursSuccessfully() {

        BusinessHoursRequest req = new BusinessHoursRequest();
        req.setDayOfWeek("MONDAY");
        req.setOpenTime(LocalTime.of(9,0));
        req.setCloseTime(LocalTime.of(18,0));
        req.setIsClosed(false);

        when(userProfileRepository.findByUser_UserId(1L))
                .thenReturn(Optional.of(profile));

        businessHoursService.addBusinessHours(1L, List.of(req));

        verify(businessHoursRepository)
                .deleteByProfile_ProfileId(profile.getProfileId());

        verify(businessHoursRepository, times(1))
                .save(any());
    }
}