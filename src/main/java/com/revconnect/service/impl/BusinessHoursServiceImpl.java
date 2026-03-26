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

// LOGGER IMPORTS
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
@RequiredArgsConstructor
public class BusinessHoursServiceImpl {

    private static final Logger logger =
            LogManager.getLogger(BusinessHoursServiceImpl.class);

    private final UserProfileRepository userProfileRepository;
    private final BusinessHoursRepository businessHoursRepository;

    public void addBusinessHours(
            Long userId,
            List<BusinessHoursRequest> requestList
    ) {

        logger.info("User {} attempting to add business hours", userId);

        UserProfile profile = userProfileRepository
                .findByUser_UserId(userId)
                .orElseThrow(() -> {
                    logger.error("Profile not found for user {}", userId);
                    return new ResourceNotFoundException("Profile not found");
                });

        // Only BUSINESS profiles allowed
        if (profile.getProfileType() != ProfileType.BUSINESS) {

            logger.warn("User {} tried to add business hours but profile type is {}",
                    userId, profile.getProfileType());

            throw new IllegalStateException(
                    "Business hours allowed only for BUSINESS profiles"
            );
        }

        // Remove old hours
        logger.info("Deleting existing business hours for profile {}", profile.getProfileId());

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

            logger.debug("Saved business hours for {} : {} - {}",
                    req.getDayOfWeek(),
                    req.getOpenTime(),
                    req.getCloseTime());
        }

        logger.info("Business hours successfully saved for user {}", userId);
    }
}