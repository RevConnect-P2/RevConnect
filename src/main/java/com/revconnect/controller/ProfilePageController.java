package com.revconnect.controller;

import com.revconnect.enums.ProfileType;
import jakarta.servlet.http.HttpSession;

import com.revconnect.entity.User;
import com.revconnect.entity.UserProfile;
import com.revconnect.enums.ConnectionStatus;
import com.revconnect.repository.UserProfileRepository;
import com.revconnect.repository.UserRepository;
import com.revconnect.service.ConnectionService;
import com.revconnect.service.FollowService;
import com.revconnect.service.PostService;
import com.revconnect.service.ProfileService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;
import java.nio.file.*;
import java.io.IOException;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class ProfilePageController {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final FollowService followService;
    private final ConnectionService connectionService;
    private final ProfileService profileService;


    // ===============================
    // PUBLIC PROFILE
    // ===============================
    @GetMapping("/profile/{username}")
    public String viewProfile(@PathVariable String username,
                              Model model,
                              HttpSession session) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfile profile = userProfileRepository
                .findByUser_UserId(user.getUserId())
                .orElse(null);

            // ===============================
    // BUSINESS HOURS (for business profiles)
    // ===============================
            if (profile != null && profile.getProfileType() == ProfileType.BUSINESS) {

            model.addAttribute(
                    "businessHours",
                    profileService.getBusinessHours(user.getUserId())
            );
        }

        long followers = followService.getFollowersCount(user.getUserId());
        long following = followService.getFollowingCount(user.getUserId());

        // logged-in user
        User loggedUser = (User) session.getAttribute("loggedUser");

        boolean isFollowing = false;
        ConnectionStatus connectionStatus = null;
        boolean canViewPosts = false;   // ✅ important

        if (loggedUser != null) {

            // follow status
            isFollowing = followService.isFollowing(
                    loggedUser.getUserId(),
                    user.getUserId()
            );

            // connection status
            connectionStatus = connectionService.getConnectionStatus(
                    loggedUser.getUserId(),
                    user.getUserId()
            );

            // ===============================
            // POST VISIBILITY LOGIC
            // ===============================

            if (profile != null && profile.getProfileVisibility() != null) {

                // OWNER CAN ALWAYS SEE POSTS
                if (loggedUser.getUserId().equals(user.getUserId())) {
                    canViewPosts = true;
                }

                // PUBLIC PROFILE
                else if ("PUBLIC".equalsIgnoreCase(profile.getProfileVisibility())) {
                    canViewPosts = true;
                }

                // PRIVATE PROFILE BUT CONNECTED
                else if ("PRIVATE".equalsIgnoreCase(profile.getProfileVisibility())
                        && connectionStatus == ConnectionStatus.ACCEPTED) {

                    canViewPosts = true;
                }
            }
        }

        // ===============================
        // MODEL ATTRIBUTES
        // ===============================
        model.addAttribute("user", user);
        model.addAttribute("profile", profile);
        model.addAttribute("followers", followers);
        model.addAttribute("following", following);
        model.addAttribute("isFollowing", isFollowing);
        model.addAttribute("connectionStatus", connectionStatus);
        model.addAttribute("canViewPosts", canViewPosts); // ✅ important

        return "profile/public-profile";
    }


    // ===============================
    // FOLLOWERS PAGE
    // ===============================
    @GetMapping("/profile/{username}/followers")
    public String viewFollowers(@PathVariable String username, Model model){

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        var followers = followService.getFollowers(user.getUserId());

        model.addAttribute("user", user);
        model.addAttribute("followersList", followers);

        return "profile/followers";
    }


    // ===============================
    // FOLLOWING PAGE
    // ===============================
    @GetMapping("/profile/{username}/following")
    public String viewFollowing(@PathVariable String username, Model model){

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        var following = followService.getFollowing(user.getUserId());

        model.addAttribute("user", user);
        model.addAttribute("followingList", following);

        return "profile/following";
    }

    @PostMapping("/profile/upload-photo")
    public String uploadProfilePhoto(@RequestParam("file") MultipartFile file,
                                     HttpSession session) throws IOException {

        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null) {
            return "redirect:/login";
        }

        UserProfile profile = userProfileRepository
                .findByUser_UserId(loggedUser.getUserId())
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        // generate unique filename
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        Path uploadPath = Paths.get("uploads");

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(fileName);

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // save path in DB
        profile.setProfilePic("/uploads/" + fileName);

        userProfileRepository.save(profile);

        return "redirect:/profile/" + loggedUser.getUsername();
    }

    @PostMapping("/profile/remove-photo")
    public String removeProfilePhoto(HttpSession session) {

        User loggedUser = (User) session.getAttribute("loggedUser");

        UserProfile profile = userProfileRepository
                .findByUser_UserId(loggedUser.getUserId())
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        // Remove profile image
        profile.setProfilePic(null);

        userProfileRepository.save(profile);

        return "redirect:/profile/" + loggedUser.getUsername();
    }

}