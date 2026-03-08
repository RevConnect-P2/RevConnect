package com.revconnect.controller;

import com.revconnect.entity.User;
import com.revconnect.entity.UserProfile;
import com.revconnect.repository.UserProfileRepository;
import com.revconnect.repository.UserRepository;
import com.revconnect.service.FollowService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class ProfilePageController {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final FollowService followService;


    // ===============================
    // PUBLIC PROFILE
    // ===============================
    @GetMapping("/profile/{username}")
    public String viewProfile(@PathVariable String username, Model model) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfile profile = userProfileRepository
                .findByUser_UserId(user.getUserId())
                .orElse(null);

        // Followers count
        long followers = followService.getFollowersCount(user.getUserId());

        // Following count
        long following = followService.getFollowingCount(user.getUserId());

        model.addAttribute("user", user);
        model.addAttribute("profile", profile);
        model.addAttribute("followers", followers);
        model.addAttribute("following", following);

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

}