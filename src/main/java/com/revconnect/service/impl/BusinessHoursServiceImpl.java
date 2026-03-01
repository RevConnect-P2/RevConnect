package com.revconnect.service.impl;

import com.revconnect.dto.request.BusinessHoursRequest;
import com.revconnect.entity.BusinessHours;
import com.revconnect.entity.UserProfile;
import com.revconnect.enums.ProfileType;
import com.revconnect.exception.ResourceNotFoundException;
import com.revconnect.repository.BusinessHoursRepository;
import com.revconnect.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BusinessHoursServiceImpl {

    private final UserProfileRepository userProfileRepository;
    private final BusinessHoursRepository businessHoursRepository;

    public void addBusinessHours(
            Long userId,
            List<BusinessHoursRequest> requestList
    ) {

        UserProfile profile = userProfileRepository
                .findByUser_UserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        // ✅ Only BUSINESS profiles allowed
        if (profile.getProfileType() != ProfileType.BUSINESS) {
            throw new IllegalStateException(
                    "Business hours allowed only for BUSINESS profiles"
            );
        }

        // Remove old hours (safe replace)
        businessHoursRepository.deleteByProfile_ProfileId(
                profile.getProfileId()
        );

        for (BusinessHoursRequest req : requestList) {

            BusinessHours hours = BusinessHours.builder()
                    .profile(profile)
                    .dayOfWeek(req.getDayOfWeek())
                    .openTime(req.getOpenTime())
                    .closeTime(req.getCloseTime())
                    .isClosed(req.getIsClosed())
                    .build();

            businessHoursRepository.save(hours);
        }
    }
}