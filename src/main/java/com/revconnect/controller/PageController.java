package com.revconnect.controller;

import com.revconnect.dto.request.ProfileUpdateRequest;
import com.revconnect.dto.request.BusinessHoursRequest;
import com.revconnect.dto.response.ProfileResponse;
import com.revconnect.dto.response.BusinessHoursResponse;
import com.revconnect.enums.ProfileType;
import com.revconnect.service.ProfileService;
import com.revconnect.service.UserService;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class PageController {

    private final ProfileService profileService;
    private final UserService userService;

    public PageController(ProfileService profileService, UserService userService) {
        this.profileService = profileService;
        this.userService = userService;
    }

    // ================= PROFILE PAGE =================
    @GetMapping("/profile")
    public String profilePage(Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = userService.getUserIdByUsername(auth.getName());

        ProfileResponse profile = profileService.getProfile(userId);
        model.addAttribute("profile", profile);

        // ✅ ADD BUSINESS HOURS (PUBLIC FOR BUSINESS PROFILES)
        if (profile.getProfileType() == ProfileType.BUSINESS) {
            List<BusinessHoursResponse> hours =
                    profileService.getBusinessHours(userId);
            model.addAttribute("businessHours", hours);
        }

        return "profile/profile";
    }

    // ================= EDIT PROFILE =================
    @GetMapping("/profile/edit")
    public String editProfilePage(Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = userService.getUserIdByUsername(auth.getName());

        ProfileResponse profile = profileService.getProfile(userId);
        model.addAttribute("profile", profile);

        return "profile/edit-profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(ProfileUpdateRequest request) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = userService.getUserIdByUsername(auth.getName());

        profileService.updateProfile(userId, request);
        return "redirect:/profile";
    }

    // ================= BUSINESS HOURS PAGE =================
    @GetMapping("/profile/business-hours")
    public String businessHoursPage(Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = userService.getUserIdByUsername(auth.getName());

        ProfileResponse profile = profileService.getProfile(userId);

        // ❌ Only BUSINESS profiles allowed
        if (profile.getProfileType() != ProfileType.BUSINESS) {
            return "redirect:/profile";
        }

        model.addAttribute("days", Arrays.asList(
                "Monday", "Tuesday", "Wednesday",
                "Thursday", "Friday", "Saturday", "Sunday"
        ));

        return "profile/business-hours";
    }

    // ================= SAVE BUSINESS HOURS =================
    @PostMapping("/profile/business-hours")
    public String saveBusinessHours(
            @RequestParam("dayOfWeek") List<String> dayOfWeek,
            @RequestParam("openTime") List<String> openTime,
            @RequestParam("closeTime") List<String> closeTime,
            @RequestParam(value = "isClosed", required = false) List<String> isClosed,
            Model model
    ) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = userService.getUserIdByUsername(auth.getName());

        List<BusinessHoursRequest> requestList = new ArrayList<>();

        try {
            for (int i = 0; i < dayOfWeek.size(); i++) {

                BusinessHoursRequest req = new BusinessHoursRequest();
                req.setDayOfWeek(dayOfWeek.get(i));

                boolean closed =
                        isClosed != null && isClosed.contains(dayOfWeek.get(i));
                req.setIsClosed(closed);

                if (!closed) {
                    if (openTime.get(i).isBlank() || closeTime.get(i).isBlank()) {
                        throw new IllegalArgumentException(
                                "Please enter both Open and Close time for " + dayOfWeek.get(i)
                        );
                    }

                    LocalTime open = LocalTime.parse(openTime.get(i));
                    LocalTime close = LocalTime.parse(closeTime.get(i));

                    if (close.isBefore(open)) {
                        throw new IllegalArgumentException(
                                "Close time cannot be before open time for " + dayOfWeek.get(i)
                        );
                    }

                    req.setOpenTime(open);
                    req.setCloseTime(close);
                }

                requestList.add(req);
            }

            profileService.addBusinessHours(userId, requestList);
            return "redirect:/profile";

        } catch (Exception e) {

            // 🔥 Show error instead of Whitelabel page
            model.addAttribute("error", e.getMessage());
            model.addAttribute("days", Arrays.asList(
                    "Monday", "Tuesday", "Wednesday",
                    "Thursday", "Friday", "Saturday", "Sunday"
            ));

            return "profile/business-hours";
        }
    }
}