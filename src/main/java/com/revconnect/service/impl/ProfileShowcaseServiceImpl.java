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

@Service
@RequiredArgsConstructor
public class ProfileShowcaseServiceImpl implements ProfileShowcaseService {

    private final UserProfileRepository userProfileRepository;
    private final ProfileShowcaseRepository showcaseRepository;

    @Override
    public void addShowcase(Long userId, ShowcaseRequest request) {

        UserProfile profile = userProfileRepository
                .findByUser_UserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        if (profile.getProfileType() == ProfileType.USER) {
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
    }

    @Override
    public List<ShowcaseResponse> getShowcases(Long userId) {

        UserProfile profile = userProfileRepository
                .findByUser_UserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        return showcaseRepository
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
    }

    @Override
    public void updateShowcase(
            Long userId,
            Long showcaseId,
            ShowcaseRequest request
    ) {

        UserProfile profile = userProfileRepository
                .findByUser_UserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        ProfileShowcase showcase = showcaseRepository
                .findByShowcaseIdAndProfile_ProfileId(
                        showcaseId,
                        profile.getProfileId()
                )
                .orElseThrow(() -> new ResourceNotFoundException("Showcase not found"));

        showcase.setTitle(request.getTitle());
        showcase.setDescription(request.getDescription());
        showcase.setPrice(request.getPrice());
        showcase.setImageUrl(request.getImageUrl());

        showcaseRepository.save(showcase);
    }

    @Override
    public void deleteShowcase(Long userId, Long showcaseId) {

        UserProfile profile = userProfileRepository
                .findByUser_UserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        ProfileShowcase showcase = showcaseRepository
                .findByShowcaseIdAndProfile_ProfileId(
                        showcaseId,
                        profile.getProfileId()
                )
                .orElseThrow(() -> new ResourceNotFoundException("Showcase not found"));

        showcaseRepository.delete(showcase);
    }
}