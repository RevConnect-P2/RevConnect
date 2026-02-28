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

        return "register";

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

            return "login";

        }

        catch (RuntimeException e)
        {

            model.addAttribute("error",
                    e.getMessage());

            return "register";

        }

    }



    // ========================
    // LOGIN PAGE
    // ========================

    @GetMapping("/login")
    public String loginPage(Model model)
    {

        model.addAttribute("loginRequest", new LoginRequest());

        return "login";

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


            return "redirect:/dashboard";

        }

        catch (RuntimeException e)
        {

            model.addAttribute("error",
                    e.getMessage());

            return "login";

        }

    }



    // ========================
    // DASHBOARD PAGE
    // ========================

    @GetMapping("/dashboard")
    public String dashboard()
    {

        return "dashboard";

    }



    // ========================
    // LOGOUT
    // ========================

    @GetMapping("/logout")
    public String logout(HttpSession session)
    {

        session.invalidate();

        return "redirect:/api/login";

    }

    // SHOW FORGOT PAGE
    @GetMapping("/forgot-password")
    public String forgotPasswordPage()
    {
        return "forgot-password";
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

            return "reset-password";

        }

        catch (RuntimeException e)
        {

            model.addAttribute("error",
                    e.getMessage());

            return "forgot-password";

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

            return "login";

        }

        catch (RuntimeException e)
        {

            model.addAttribute("error",
                    e.getMessage());

            model.addAttribute("email", email);

            model.addAttribute("question",
                    authService.findByEmail(email)
                            .getSecurityQuestion());

            return "reset-password";

        }

    }
}