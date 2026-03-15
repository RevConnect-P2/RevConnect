package com.revconnect.controller;

import com.revconnect.entity.User;
import com.revconnect.service.ConnectionService;
import com.revconnect.service.FollowService;
import com.revconnect.service.PostService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.revconnect.entity.UserProfile;
import com.revconnect.repository.UserProfileRepository;

@Controller
public class MyPostsPageController {

    private final FollowService followService;
    private final ConnectionService connectionService;
    private final PostService postService;
    private final UserProfileRepository userProfileRepository;

    public MyPostsPageController(FollowService followService,
                                 ConnectionService connectionService,
                                 PostService postService,
                                 UserProfileRepository userProfileRepository) {

        this.followService = followService;
        this.connectionService = connectionService;
        this.postService = postService;
        this.userProfileRepository = userProfileRepository;
    }

    @GetMapping("/posts")
    public String myPostsPage(HttpSession session, Model model) {

        User user = (User) session.getAttribute("loggedUser");
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        UserProfile userProfile = userProfileRepository
                .findByUser_UserId(user.getUserId())
                .orElse(null);

        model.addAttribute("userProfile", userProfile);

        long followers =
                followService.getFollowersCount(user.getUserId());

        long following =
                followService.getFollowingCount(user.getUserId());

        long connections =
                connectionService.getConnectionsCount(user.getUserId());

        model.addAttribute("followersCount", followers);
        model.addAttribute("followingCount", following);
        model.addAttribute("connectionsCount", connections);

        model.addAttribute(
                "trendingTags",
                postService.getTrendingHashtags()
        );
        return "posts/my-posts";
    }
}