package com.revconnect.service.impl;

import com.revconnect.dto.request.ShowcaseRequest;
import com.revconnect.dto.response.ShowcaseResponse;
import com.revconnect.entity.ProfileShowcase;
import com.revconnect.entity.UserProfile;
import com.revconnect.enums.ProfileType;
import com.revconnect.exception.ResourceNotFoundException;
import com.revconnect.repository.ProfileShowcaseRepository;
import com.revconnect.repository.UserProfileRepository;
import com.revconnect.service.ProfileShowcaseService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

// LOGGER IMPORTS
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
@RequiredArgsConstructor
public class ProfileShowcaseServiceImpl implements ProfileShowcaseService {

    private static final Logger logger =
            LogManager.getLogger(ProfileShowcaseServiceImpl.class);

    private final UserProfileRepository userProfileRepository;
    private final ProfileShowcaseRepository showcaseRepository;

    // ADD SHOWCASE
    @Override
    public void addShowcase(Long userId, ShowcaseRequest request) {

        logger.info("User {} attempting to add showcase: {}", userId, request.getTitle());

        UserProfile profile = userProfileRepository
                .findByUser_UserId(userId)
                .orElseThrow(() -> {
                    logger.error("Profile not found for user {}", userId);
                    return new ResourceNotFoundException("Profile not found");
                });

        if (profile.getProfileType() == ProfileType.PERSONAL) {

            logger.warn("User {} attempted to add showcase for PERSONAL profile", userId);

            throw new IllegalStateException(
                    "Showcase allowed only for CREATOR or BUSINESS profiles"
            );
        }

        ProfileShowcase showcase = ProfileShowcase.builder()
                .profile(profile)
                .title(request.getTitle())
                .description(request.getDescription())
                .price(request.getPrice())
                .imageUrl(request.getImageUrl())
                .build();

        showcaseRepository.save(showcase);

        logger.info("Showcase '{}' created successfully for user {}", request.getTitle(), userId);
    }


    // GET SHOWCASES

    @Override
    public List<ShowcaseResponse> getShowcases(Long userId) {

        logger.info("Fetching showcases for user {}", userId);

        UserProfile profile = userProfileRepository
                .findByUser_UserId(userId)
                .orElseThrow(() -> {
                    logger.error("Profile not found for user {}", userId);
                    return new ResourceNotFoundException("Profile not found");
                });

        List<ShowcaseResponse> showcases =
                showcaseRepository
                        .findByProfile_ProfileId(profile.getProfileId())
                        .stream()
                        .map(s -> new ShowcaseResponse(
                                s.getShowcaseId(),
                                s.getTitle(),
                                s.getDescription(),
                                s.getPrice(),
                                s.getImageUrl()
                        ))
                        .toList();

        logger.info("User {} has {} showcases", userId, showcases.size());

        return showcases;
    }


    // UPDATE SHOWCASE

    @Override
    public void updateShowcase(
            Long userId,
            Long showcaseId,
            ShowcaseRequest request
    ) {

        logger.info("User {} updating showcase {}", userId, showcaseId);

        UserProfile profile = userProfileRepository
                .findByUser_UserId(userId)
                .orElseThrow(() -> {
                    logger.error("Profile not found for user {}", userId);
                    return new ResourceNotFoundException("Profile not found");
                });

        ProfileShowcase showcase = showcaseRepository
                .findByShowcaseIdAndProfile_ProfileId(
                        showcaseId,
                        profile.getProfileId()
                )
                .orElseThrow(() -> {
                    logger.error("Showcase {} not found for user {}", showcaseId, userId);
                    return new ResourceNotFoundException("Showcase not found");
                });

        showcase.setTitle(request.getTitle());
        showcase.setDescription(request.getDescription());
        showcase.setPrice(request.getPrice());
        showcase.setImageUrl(request.getImageUrl());

        showcaseRepository.save(showcase);

        logger.info("Showcase {} updated successfully by user {}", showcaseId, userId);
    }


    // DELETE SHOWCASE

    @Override
    public void deleteShowcase(Long userId, Long showcaseId) {

        logger.info("User {} attempting to delete showcase {}", userId, showcaseId);

        UserProfile profile = userProfileRepository
                .findByUser_UserId(userId)
                .orElseThrow(() -> {
                    logger.error("Profile not found for user {}", userId);
                    return new ResourceNotFoundException("Profile not found");
                });

        ProfileShowcase showcase = showcaseRepository
                .findByShowcaseIdAndProfile_ProfileId(
                        showcaseId,
                        profile.getProfileId()
                )
                .orElseThrow(() -> {
                    logger.error("Showcase {} not found for user {}", showcaseId, userId);
                    return new ResourceNotFoundException("Showcase not found");
                });

        showcaseRepository.delete(showcase);

        logger.info("Showcase {} deleted successfully by user {}", showcaseId, userId);
    }
}