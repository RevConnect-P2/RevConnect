package com.revconnect.service;

import com.revconnect.dto.request.BusinessHoursRequest;
import com.revconnect.dto.request.ProfileCreateRequest;
import com.revconnect.dto.request.ProfileUpdateRequest;
import com.revconnect.dto.response.BusinessHoursResponse;
import com.revconnect.dto.response.ProfileResponse;

import java.util.List;

public interface ProfileService {

    ProfileResponse createProfile(Long userId, ProfileCreateRequest request);

    ProfileResponse updateProfile(Long userId, ProfileUpdateRequest request);

    ProfileResponse getProfile(Long userId);

    List<ProfileResponse> searchProfiles(String query);

    // 🔥 ADD THIS (THIS WAS MISSING)
    void addBusinessHours(
            Long userId,
            List<BusinessHoursRequest> requestList
    );

    List<BusinessHoursResponse> getBusinessHours(Long userId);

    void updateBusinessHours(
            Long userId,
            String dayOfWeek,
            BusinessHoursRequest request
    );

    void deleteBusinessHours(Long userId, String dayOfWeek);

    void deleteAllBusinessHours(Long userId);
}