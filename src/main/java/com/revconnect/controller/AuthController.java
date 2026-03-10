package com.revconnect.controller;

import com.revconnect.dto.request.LoginRequest;
import com.revconnect.dto.request.RegisterRequest;
import com.revconnect.entity.User;
import com.revconnect.service.AuthService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

// ✅ LOGGER IMPORTS
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Controller
public class AuthController {

    // ✅ LOGGER OBJECT
    private static final Logger logger = LogManager.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;



    // ========================
    // REGISTER PAGE
    // ========================

    @GetMapping("/register")
    public String registerPage(Model model)
    {

        logger.info("Register page requested");

        model.addAttribute("registerRequest", new RegisterRequest());

        return "auth/register";

    }



    // ========================
    // REGISTER USER
    // ========================

    @PostMapping("/register")
    public String register(
            @ModelAttribute RegisterRequest request,
            Model model)
    {

        try {

            logger.info("Register request received for email: {}", request.getEmail());

            authService.register(request);

            logger.info("User registered successfully: {}", request.getEmail());

            model.addAttribute("success",
                    "Registration successful. Please login.");

            return "auth/login";

        }

        catch (RuntimeException e)
        {

            logger.error("Registration failed for email: {}", request.getEmail(), e);

            model.addAttribute("error",
                    e.getMessage());

            return "auth/register";

        }

    }



    // ========================
    // LOGIN PAGE
    // ========================

    @GetMapping("/login")
    public String loginPage(Model model)
    {

        logger.info("Login page requested");

        model.addAttribute("loginRequest", new LoginRequest());

        return "auth/login";

    }



    // ========================
    // LOGIN USER
    // ========================

    @PostMapping("/login")
    public String login(
            @ModelAttribute LoginRequest request,
            HttpSession session,
            Model model)
    {

        try {

            logger.info("Login attempt for email: {}", request.getEmail());

            User user = authService.login(request);

            logger.info("Login successful for email: {}", request.getEmail());


            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            user.getEmail(),
                            null,
                            null
                    );

            SecurityContextHolder.getContext()
                    .setAuthentication(authToken);

            session.setAttribute(
                    "SPRING_SECURITY_CONTEXT",
                    SecurityContextHolder.getContext()
            );


            session.setAttribute("loggedUser", user);


            return "redirect:/dashboard";

        }

        catch (RuntimeException e)
        {

            logger.error("Login failed for email: {}", request.getEmail(), e);

            model.addAttribute("error", e.getMessage());

            return "auth/login";

        }

    }



    // ========================
    // LOGOUT
    // ========================

    @GetMapping("/logout")
    public String logout(HttpSession session, Model model)
    {

        logger.info("User logout requested");

        SecurityContextHolder.clearContext();

        session.invalidate();

        model.addAttribute("success", "Logout Successful");

        logger.info("User logged out successfully");

        return "auth/login";

    }



    // SHOW FORGOT PAGE
    @GetMapping("/forgot-password")
    public String forgotPasswordPage()
    {

        logger.info("Forgot password page requested");

        return "auth/forgot-password";
    }



    // GET QUESTION FOR FORGET
    @PostMapping("/forgot-password")
    public String getQuestion(@RequestParam String email,
                              Model model)
    {

        try
        {

            logger.info("Security question requested for email: {}", email);

            User user = authService.findByEmail(email);

            model.addAttribute("email", email);

            model.addAttribute("question",
                    user.getSecurityQuestion());

            return "auth/reset-password";

        }

        catch (RuntimeException e)
        {

            logger.error("Failed to fetch security question for email: {}", email, e);

            model.addAttribute("error",
                    e.getMessage());

            return "auth/forgot-password";

        }

    }



    // RESET PASSWORD
    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String email,
                                @RequestParam String answer,
                                @RequestParam String newPassword,
                                Model model)
    {

        try
        {

            logger.info("Password reset attempt for email: {}", email);

            authService.resetPassword(email, answer, newPassword);

            logger.info("Password reset successful for email: {}", email);

            model.addAttribute("success",
                    "Password reset successful. Please login.");

            return "auth/login";

        }

        catch (RuntimeException e)
        {

            logger.error("Password reset failed for email: {}", email, e);

            model.addAttribute("error",
                    e.getMessage());

            model.addAttribute("email", email);

            model.addAttribute("question",
                    authService.findByEmail(email)
                            .getSecurityQuestion());

            return "auth/reset-password";

        }

    }
}