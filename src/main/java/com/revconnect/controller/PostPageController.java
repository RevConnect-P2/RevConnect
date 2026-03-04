package com.revconnect.controller;

import com.revconnect.dto.response.PostResponse;
import com.revconnect.entity.User;
import com.revconnect.service.PostService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostPageController {

    private final PostService postService;

    @GetMapping("/create")
    public String showCreatePostPage(HttpSession session, Model model) {

        // Assuming user is stored in session after login
        User loggedInUser = (User) session.getAttribute("loggedUser");

        if (loggedInUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("userType", loggedInUser.getUserType());

        return "posts/create-post";
    }

    @GetMapping("/edit/{postId}")
    public String showEditPostPage(
            @PathVariable Long postId,
            HttpSession session,
            Model model
    ) {
        User loggedInUser = (User) session.getAttribute("loggedUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }

        PostResponse post = postService.getPostById(postId);

        // SECURITY: only owner can edit
        if (!post.getUserId().equals(loggedInUser.getUserId())) {
            return "redirect:/dashboard";
        }

        model.addAttribute("user", loggedInUser);
        model.addAttribute("userType", loggedInUser.getUserType());
        model.addAttribute("post", post);     // 🔥 IMPORTANT
        model.addAttribute("isEdit", true);   // 🔥 FLAG

        return "posts/create-post";
    }
}