
package com.revconnect.controller;

import com.revconnect.dto.request.LoginRequest;
import com.revconnect.dto.request.RegisterRequest;
import com.revconnect.entity.User;
import com.revconnect.service.AuthService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
public class AuthController {

    @Autowired
    private AuthService authService;



    // ========================
    // REGISTER PAGE
    // ========================

    @GetMapping("/register")
    public String registerPage(Model model)
    {

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

            authService.register(request);

            model.addAttribute("success",
                    "Registration successful. Please login.");

            return "auth/login";

        }

        catch (RuntimeException e)
        {

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

            User user = authService.login(request);


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


            // ✅ ADD THIS LINE
            session.setAttribute("loggedUser", user);


            return "redirect:/dashboard";

        }

        catch (RuntimeException e)
        {

            model.addAttribute("error", e.getMessage());

            return "auth/login";

        }

    }

    // ========================
    // LOGOUT
    // ========================

    // ========================
// LOGOUT
// ========================

    @GetMapping("/logout")
    public String logout(HttpSession session, Model model)
    {

        // Clear Spring Security context
        SecurityContextHolder.clearContext();

        // Invalidate session
        session.invalidate();

        // Send success message to login page
        model.addAttribute("success", "Logout Successful");

        // Return login page directly (NOT redirect)
        return "auth/login";

    }

    // SHOW FORGOT PAGE
    @GetMapping("/forgot-password")
    public String forgotPasswordPage()
    {
        return "auth/forgot-password";
    }


    // GET QUESTION FOR FORGET
    @PostMapping("/forgot-password")
    public String getQuestion(@RequestParam String email,
                              Model model)
    {

        try
        {

            User user = authService.findByEmail(email);

            model.addAttribute("email", email);

            model.addAttribute("question",
                    user.getSecurityQuestion());

            return "auth/reset-password";

        }

        catch (RuntimeException e)
        {

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

            authService.resetPassword(email, answer, newPassword);

            model.addAttribute("success",
                    "Password reset successful. Please login.");

            return "auth/login";

        }

        catch (RuntimeException e)
        {

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
