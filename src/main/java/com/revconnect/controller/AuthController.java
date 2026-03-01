package com.revconnect.controller;

import com.revconnect.dto.request.LoginRequest;
import com.revconnect.dto.request.RegisterRequest;
import com.revconnect.entity.User;
import com.revconnect.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
public class AuthController {

    @Autowired
    private AuthService authService;

    // ========================
    // REGISTER PAGE
    // ========================
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "auth/register";
    }

    // ========================
    // REGISTER USER
    // ========================
    @PostMapping("/register")
    public String register(@ModelAttribute RegisterRequest request, Model model) {
        try {
            authService.register(request);
            model.addAttribute("success", "Registration successful. Please login.");
            return "auth/login";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        }
    }

    // ========================
    // LOGIN PAGE
    // ========================
    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "auth/login";
    }

    // ========================
    // LOGIN USER (MANUAL AUTH)
    // ========================
    @PostMapping("/login")
    public String login(
            @ModelAttribute LoginRequest request,
            HttpSession session,
            Model model
    ) {
        try {
            User user = authService.login(request);

            // Store user info in session
            session.setAttribute("USER_ID", user.getUserId());
            session.setAttribute("USERNAME", user.getUsername());

            return "redirect:/dashboard";

        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/login";
        }
    }

    // ========================
    // LOGOUT
    // ========================
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    // ========================
    // FORGOT PASSWORD
    // ========================
    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String getQuestion(@RequestParam String email, Model model) {
        try {
            User user = authService.findByEmail(email);
            model.addAttribute("email", email);
            model.addAttribute("question", user.getSecurityQuestion());
            return "auth/reset-password";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/forgot-password";
        }
    }

    @PostMapping("/reset-password")
    public String resetPassword(
            @RequestParam String email,
            @RequestParam String answer,
            @RequestParam String newPassword,
            Model model
    ) {
        try {
            authService.resetPassword(email, answer, newPassword);
            model.addAttribute("success", "Password reset successful. Please login.");
            return "auth/login";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("email", email);
            model.addAttribute(
                    "question",
                    authService.findByEmail(email).getSecurityQuestion()
            );
            return "auth/reset-password";
        }
    }

    // ========================
    // LOGGED-IN USER INFO (FOR JS)
    // ========================
    @GetMapping("/auth/me")
    @ResponseBody
    public Map<String, Object> getLoggedInUser(HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        Object userId = session.getAttribute("USER_ID");
        Object username = session.getAttribute("USERNAME");

        if (userId == null || username == null) {
            response.put("authenticated", false);
            return response;
        }

        response.put("authenticated", true);
        response.put("userId", userId);
        response.put("username", username);
        return response;
    }
}