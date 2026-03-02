package com.revconnect.service.impl;

import com.revconnect.dto.request.BusinessHoursRequest;
import com.revconnect.dto.request.ProfileCreateRequest;
import com.revconnect.dto.request.ProfileUpdateRequest;
import com.revconnect.dto.response.BusinessHoursResponse;
import com.revconnect.dto.response.ProfileResponse;
import com.revconnect.entity.BusinessHours;
import com.revconnect.entity.User;
import com.revconnect.entity.UserProfile;
import com.revconnect.enums.ProfileType;
import com.revconnect.exception.ResourceNotFoundException;
import com.revconnect.repository.BusinessHoursRepository;
import com.revconnect.repository.UserProfileRepository;
import com.revconnect.repository.UserRepository;
import com.revconnect.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final BusinessHoursRepository businessHoursRepository;

    // ================= CREATE PROFILE =================
    @Override
    public ProfileResponse createProfile(Long userId, ProfileCreateRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (userProfileRepository.findByUser_UserId(userId).isPresent()) {
            throw new IllegalStateException("Profile already exists");
        }

        // ✅ Profile type ALWAYS comes from USER
        ProfileType profileType = ProfileType.valueOf(user.getUserType());

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

        // ❌ DO NOT validate profileType
        // ❌ DO NOT change profileType

        profile.setFullName(request.getFullName());
        profile.setBio(request.getBio());
        profile.setProfilePic(request.getProfilePic());
        profile.setLocation(request.getLocation());
        profile.setWebsite(request.getWebsite());
        profile.setProfileVisibility(request.getProfileVisibility());

        applyProfileTypeRules(profile, request);

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
                            ProfileType.valueOf(user.getUserType());

                    UserProfile newProfile = UserProfile.builder()
                            .user(user)
                            .fullName(user.getUsername())
                            .bio("")
                            .profilePic(null)
                            .location(null)
                            .website(null)
                            .profileVisibility("PUBLIC")
                            .profileType(profileType)   // ✅ FIXED
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

    @Override
    public void addBusinessHours(
            Long userId,
            List<BusinessHoursRequest> requestList
    ) {

        UserProfile profile = userProfileRepository
                .findByUser_UserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        // ❌ Only BUSINESS profiles allowed
        if (profile.getProfileType() != ProfileType.BUSINESS) {
            throw new IllegalStateException(
                    "Business hours allowed only for BUSINESS profiles"
            );
        }

        // Remove existing hours (safe replace)
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
    @Override
    public List<BusinessHoursResponse> getBusinessHours(Long userId) {

        UserProfile profile = userProfileRepository
                .findByUser_UserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        return businessHoursRepository
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
                .toList();
    }

    @Override
    public void updateBusinessHours(
            Long userId,
            String dayOfWeek,
            BusinessHoursRequest request
    ) {

        UserProfile profile = userProfileRepository
                .findByUser_UserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        if (profile.getProfileType() != ProfileType.BUSINESS) {
            throw new IllegalStateException(
                    "Business hours allowed only for BUSINESS profiles"
            );
        }

        BusinessHours hours = businessHoursRepository
                .findByProfile_ProfileId(profile.getProfileId())
                .stream()
                .filter(h -> h.getDayOfWeek().equalsIgnoreCase(dayOfWeek))
                .findFirst()
                .orElseThrow(() ->
                        new ResourceNotFoundException("Business hours not found for " + dayOfWeek)
                );

        hours.setOpenTime(request.getOpenTime());
        hours.setCloseTime(request.getCloseTime());
        hours.setIsClosed(request.getIsClosed());

        businessHoursRepository.save(hours);
    }

    @Override
    public void deleteBusinessHours(Long userId, String dayOfWeek) {

        UserProfile profile = userProfileRepository
                .findByUser_UserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        if (profile.getProfileType() != ProfileType.BUSINESS) {
            throw new IllegalStateException(
                    "Business hours allowed only for BUSINESS profiles"
            );
        }

        BusinessHours hours = businessHoursRepository
                .findByProfile_ProfileId(profile.getProfileId())
                .stream()
                .filter(h -> h.getDayOfWeek().equalsIgnoreCase(dayOfWeek))
                .findFirst()
                .orElseThrow(() ->
                        new ResourceNotFoundException("Business hours not found for " + dayOfWeek)
                );

        businessHoursRepository.delete(hours);
    }

    @Override
    @Transactional
    public void deleteAllBusinessHours(Long userId) {

        UserProfile profile = userProfileRepository
                .findByUser_UserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        if (profile.getProfileType() != ProfileType.BUSINESS) {
            throw new IllegalStateException(
                    "Business hours allowed only for BUSINESS profiles"
            );
        }

        businessHoursRepository.deleteByProfile_ProfileId(
                profile.getProfileId()
        );
    }

    // ================= PROFILE TYPE RULE ENGINE =================
    private void applyProfileTypeRules(UserProfile profile, Object request) {

        profile.setCategory(null);
        profile.setExternalLinks(null);
        profile.setBusinessAddress(null);
        profile.setContactInfo(null);

        ProfileType type = profile.getProfileType();

        if (type == ProfileType.CREATOR) {
            if (request instanceof ProfileCreateRequest r) {
                profile.setCategory(r.getCategory());
                profile.setExternalLinks(r.getExternalLinks());
            } else if (request instanceof ProfileUpdateRequest r) {
                profile.setCategory(r.getCategory());
                profile.setExternalLinks(r.getExternalLinks());
            }
        }

        if (type == ProfileType.BUSINESS) {
            if (request instanceof ProfileCreateRequest r) {
                profile.setBusinessAddress(r.getBusinessAddress());
                profile.setContactInfo(r.getContactInfo());
            } else if (request instanceof ProfileUpdateRequest r) {
                profile.setBusinessAddress(r.getBusinessAddress());
                profile.setContactInfo(r.getContactInfo());
            }
        }
    }

    // ================= RESPONSE MAPPER =================
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

        return response;
    }
}