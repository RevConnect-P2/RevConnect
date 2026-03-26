package com.revconnect.service.impl;

import com.revconnect.dto.request.BusinessHoursRequest;
import com.revconnect.dto.request.ProfileCreateRequest;
import com.revconnect.dto.request.ProfileUpdateRequest;
import com.revconnect.dto.response.BusinessHoursResponse;
import com.revconnect.dto.response.ProfileAnalyticsResponse;
import com.revconnect.dto.response.ProfileResponse;
import com.revconnect.entity.BusinessHours;
import com.revconnect.entity.User; // ✅ FIXED IMPORT
import com.revconnect.entity.UserProfile;
import com.revconnect.enums.ProfileType;
import com.revconnect.exception.ResourceNotFoundException;
import com.revconnect.repository.BusinessHoursRepository;
import com.revconnect.repository.PostRepository;
import com.revconnect.repository.UserProfileRepository;
import com.revconnect.repository.UserRepository;
import com.revconnect.service.AnalyticsService;
import com.revconnect.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final BusinessHoursRepository businessHoursRepository;
    private final AnalyticsService analyticsService;
    private final PostRepository postRepository;
    // ================= CREATE PROFILE =================

    @Override
    public ProfileResponse createProfile(Long userId, ProfileCreateRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (userProfileRepository.findByUser_UserId(userId).isPresent()) {
            throw new IllegalStateException("Profile already exists");
        }

        ProfileType profileType = resolveProfileType(user.getUserType());

        UserProfile profile = UserProfile.builder()
                .user(user)
                .fullName(request.getFullName())
                .bio(request.getBio())
                .profilePic(request.getProfilePic())
                .location(request.getLocation())
                .website(request.getWebsite())
                .profileVisibility(
                        request.getProfileVisibility() != null
                                ? request.getProfileVisibility()
                                : "PUBLIC"
                )
                .profileType(profileType)
                .category("GENERAL") // ✅ FIX
                .build();

        applyProfileTypeRules(profile, request);

        UserProfile savedProfile = userProfileRepository.save(profile);

        return mapToResponse(user, savedProfile);
    }


    // ================= UPDATE PROFILE =================

    @Override
    public ProfileResponse updateProfile(Long userId, ProfileUpdateRequest request) {

        UserProfile profile = userProfileRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        // Basic fields
        profile.setFullName(request.getFullName());
        profile.setBio(request.getBio());
        profile.setProfilePic(request.getProfilePic());
        profile.setLocation(request.getLocation());
        profile.setWebsite(request.getWebsite());

        profile.setProfileVisibility(
                request.getProfileVisibility() != null
                        ? request.getProfileVisibility()
                        : "PUBLIC"
        );

        // Apply BUSINESS / CREATOR rules
        applyProfileTypeRules(profile, request);

        // Save
        UserProfile updatedProfile = userProfileRepository.save(profile);

        return mapToResponse(profile.getUser(), updatedProfile);
    }

    // ================= GET PROFILE =================

    @Override
    public ProfileResponse getProfile(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UserProfile profile = userProfileRepository.findByUser_UserId(userId)
                .orElseGet(() -> {

                    ProfileType profileType =
                            resolveProfileType(user.getUserType());

                    UserProfile newProfile = UserProfile.builder()
                            .user(user)
                            .fullName(user.getUsername())
                            .bio("")
                            .profileVisibility("PUBLIC")
                            .profileType(profileType)
                            .category("GENERAL") // ✅ FIX
                            .build();

                    return userProfileRepository.save(newProfile);
                });

        return mapToResponse(user, profile);
    }


    // ================= SEARCH =================

    @Override
    public List<ProfileResponse> searchProfiles(String query) {

        return userProfileRepository
                .findByUser_UsernameContainingIgnoreCaseOrFullNameContainingIgnoreCase(query, query)
                .stream()
                .filter(p -> "PUBLIC".equalsIgnoreCase(p.getProfileVisibility()))
                .map(p -> mapToResponse(p.getUser(), p))
                .toList();
    }


    // ================= ADD BUSINESS HOURS =================

    @Override
    @Transactional
    public void addBusinessHours(Long userId, List<BusinessHoursRequest> requestList) {

        UserProfile profile = userProfileRepository
                .findByUser_UserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        if (profile.getProfileType() != ProfileType.BUSINESS) {
            throw new IllegalStateException("Only BUSINESS profile allowed");
        }

        businessHoursRepository.deleteByProfile_ProfileId(profile.getProfileId());
        businessHoursRepository.flush();

        Set<String> seenDays = new HashSet<>();

        for (BusinessHoursRequest req : requestList) {

            if (!seenDays.add(req.getDayOfWeek())) continue;

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


    // ================= UPDATE BUSINESS HOURS (✅ FIXED) =================

    @Override
    @Transactional
    public void updateBusinessHours(Long userId, String dayOfWeek, BusinessHoursRequest request) {

        UserProfile profile = userProfileRepository
                .findByUser_UserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        if (profile.getProfileType() != ProfileType.BUSINESS) {
            throw new IllegalStateException("Only BUSINESS profile allowed");
        }

        BusinessHours hours = businessHoursRepository
                .findByProfile_ProfileId(profile.getProfileId())
                .stream()
                .filter(h -> h.getDayOfWeek().equalsIgnoreCase(dayOfWeek))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Business hours not found"));

        hours.setOpenTime(request.getOpenTime());
        hours.setCloseTime(request.getCloseTime());
        hours.setIsClosed(request.getIsClosed());

        businessHoursRepository.save(hours);
    }


    // ================= GET BUSINESS HOURS =================
    @Override
    public List<BusinessHoursResponse> getBusinessHours(Long userId) {

        UserProfile profile = userProfileRepository
                .findByUser_UserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        List<BusinessHoursResponse> hours = businessHoursRepository
                .findByProfile_ProfileId(profile.getProfileId())
                .stream()
                .map(h -> {

                    BusinessHoursResponse res = new BusinessHoursResponse();

                    res.setDayOfWeek(h.getDayOfWeek());
                    res.setOpenTime(h.getOpenTime());
                    res.setCloseTime(h.getCloseTime());
                    res.setIsClosed(h.getIsClosed());

                    return res;
                })

                .collect(java.util.stream.Collectors.toList());




        hours.sort(java.util.Comparator.comparing(
                h -> java.time.DayOfWeek.valueOf(h.getDayOfWeek().toUpperCase())
        ));

        return hours;
    }

// ================= RULE ENGINE =================

    private void applyProfileTypeRules(UserProfile profile, Object request) {

        ProfileType type = profile.getProfileType();


        // ================= CREATOR =================

        if (type == ProfileType.CREATOR) {

            if (request instanceof ProfileCreateRequest r) {

                profile.setCategory(
                        r.getCategory() != null ? r.getCategory() : "GENERAL"
                );

                profile.setExternalLinks(r.getExternalLinks());

            }


            if (request instanceof ProfileUpdateRequest r) {

                profile.setCategory(
                        r.getCategory() != null ? r.getCategory() : "GENERAL"
                );

                profile.setExternalLinks(r.getExternalLinks());

            }

        }


        // ================= BUSINESS =================

        else if (type == ProfileType.BUSINESS) {




            if (request instanceof ProfileCreateRequest r) {

                profile.setBusinessAddress(
                        r.getBusinessAddress() != null ? r.getBusinessAddress() : ""
                );

                profile.setContactInfo(
                        r.getContactInfo() != null ? r.getContactInfo() : ""
                );

            }



            if (request instanceof ProfileUpdateRequest r) {

                profile.setBusinessAddress(
                        r.getBusinessAddress() != null ? r.getBusinessAddress() : ""
                );

                profile.setContactInfo(
                        r.getContactInfo() != null ? r.getContactInfo() : ""
                );

            }

        }


        // ================= PERSONAL =================

        else {

            profile.setCategory("GENERAL");

        }

    }


    // ================= RESPONSE =================

    private ProfileResponse mapToResponse(User user, UserProfile profile) {

        ProfileResponse response = new ProfileResponse();

        response.setUserId(user.getUserId());
        response.setUsername(user.getUsername());
        response.setFullName(profile.getFullName());
        response.setBio(profile.getBio());
        response.setProfilePic(profile.getProfilePic());
        response.setLocation(profile.getLocation());
        response.setWebsite(profile.getWebsite());
        response.setProfileVisibility(profile.getProfileVisibility());
        response.setProfileType(profile.getProfileType());
        response.setCategory(profile.getCategory());
        response.setExternalLinks(profile.getExternalLinks());
        response.setBusinessAddress(profile.getBusinessAddress());
        response.setContactInfo(profile.getContactInfo());

        // ===============================
        // ADD ANALYTICS HERE
        // ===============================
        ProfileAnalyticsResponse analytics =
                analyticsService.getUserAnalytics(user.getUserId());

        response.setTotalLikes(analytics.getTotalLikes());
        response.setTotalComments(analytics.getTotalComments());
        response.setTotalShares(analytics.getTotalShares());

        // total posts
        response.setTotalPosts(postRepository.countByUser(user));
        return response;
    }

// ================= TYPE RESOLVER =================

    private ProfileType resolveProfileType(String userType) {

        return ProfileType.fromString(userType);

    }

    @Override
    @Transactional
    public void deleteBusinessHours(Long userId, String dayOfWeek) {

        UserProfile profile = userProfileRepository
                .findByUser_UserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Profile not found")
                );

        if (profile.getProfileType() != ProfileType.BUSINESS) {
            throw new IllegalStateException(
                    "Only BUSINESS profile allowed"
            );
        }

        BusinessHours hours = businessHoursRepository
                .findByProfile_ProfileId(profile.getProfileId())
                .stream()
                .filter(h ->
                        h.getDayOfWeek().equalsIgnoreCase(dayOfWeek)
                )
                .findFirst()
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Business hours not found for " + dayOfWeek
                        )
                );

        businessHoursRepository.delete(hours);
    }
    @Override
    @Transactional
    public void deleteAllBusinessHours(Long userId) {

        UserProfile profile = userProfileRepository
                .findByUser_UserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Profile not found")
                );

        if (profile.getProfileType() != ProfileType.BUSINESS) {
            throw new IllegalStateException(
                    "Only BUSINESS profile allowed"
            );
        }

        businessHoursRepository.deleteByProfile_ProfileId(
                profile.getProfileId()
        );

    }

}